using System.Security.Claims;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

public static class GroupInviteEndpoints
{
    public static IEndpointRouteBuilder MapGroupInviteEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapPost("/v1/groups/{groupId:guid}/invites", async (Guid groupId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await EndpointSupport.OwnedGroup(db, groupId, user);
            if (group is null) return Results.Forbid();
            var invite = new GroupInvite { GroupId = groupId, Code = Convert.ToHexString(Guid.NewGuid().ToByteArray())[..10], ExpiresAt = DateTimeOffset.UtcNow.AddDays(7) };
            db.GroupInvites.Add(invite);
            await db.SaveChangesAsync();
            return Results.Created($"/v1/group-invites/{invite.Id}", new { invite.Code, invite.GroupId, invite.ExpiresAt });
        }).RequireAuthorization();
        app.MapDelete("/v1/groups/{groupId:guid}/invites/{inviteId:guid}", async (Guid groupId, Guid inviteId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            if (await EndpointSupport.OwnedGroup(db, groupId, user) is null) return Results.Forbid();
            var invite = await db.GroupInvites.SingleOrDefaultAsync(x => x.Id == inviteId && x.GroupId == groupId);
            if (invite is null) return Results.NotFound();
            invite.Revoked = true;
            await db.SaveChangesAsync();
            return Results.NoContent();
        }).RequireAuthorization();
        app.MapPost("/v1/group-memberships", async (JoinInviteRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var invite = await db.GroupInvites.SingleOrDefaultAsync(x => x.Code == request.InviteCode && !x.Revoked && x.ConsumedAt == null && x.ExpiresAt > DateTimeOffset.UtcNow);
            if (invite is null) return Results.NotFound();
            if (await db.GroupMembers.AnyAsync(x => x.GroupId == invite.GroupId && x.UserId == user.UserId())) return Results.Conflict();
            invite.ConsumedAt = DateTimeOffset.UtcNow;
            db.GroupMembers.Add(new GroupMember { GroupId = invite.GroupId, UserId = user.UserId(), Role = GroupRole.Member });
            await db.SaveChangesAsync();
            return Results.Created($"/v1/groups/{invite.GroupId}", new { invite.GroupId });
        }).RequireAuthorization();
        return app;
    }
}
