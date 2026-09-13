using System.Security.Claims;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

public static class GroupEndpoints
{
    public static IEndpointRouteBuilder MapGroupEndpoints(this IEndpointRouteBuilder app)
    {
        var routes = app.MapGroup("/v1/groups").RequireAuthorization();
        routes.MapGet("/", async (ClaimsPrincipal user, HackatudoDbContext db) =>
            Results.Ok(await db.Groups.AsNoTracking().Include(x => x.Mascot).Where(x => x.Members.Any(m => m.UserId == user.UserId() && m.Status == MembershipStatus.Active)).ToListAsync()));
        routes.MapPost("/", async (CreateGroupRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            if (string.IsNullOrWhiteSpace(request.Name) || string.IsNullOrWhiteSpace(request.Objective)) return Results.BadRequest();
            var group = new StudyGroup { Name = request.Name.Trim(), Objective = request.Objective.Trim(), OwnerId = user.UserId() };
            group.Members.Add(new GroupMember { GroupId = group.Id, UserId = group.OwnerId, Role = GroupRole.Owner });
            group.Mascot = new MascotProgress { GroupId = group.Id, Type = request.MascotType.Trim() };
            db.Groups.Add(group);
            await db.SaveChangesAsync();
            return Results.Created($"/v1/groups/{group.Id}", group);
        });
        routes.MapGet("/{groupId:guid}", async (Guid groupId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await db.Groups.AsNoTracking().Include(x => x.Members).Include(x => x.Mascot).Include(x => x.Goals)
                .SingleOrDefaultAsync(x => x.Id == groupId && x.Members.Any(m => m.UserId == user.UserId() && m.Status == MembershipStatus.Active));
            return group is null ? Results.NotFound() : Results.Ok(group);
        });
        routes.MapPost("/{groupId:guid}/members", async (Guid groupId, AddMemberRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await EndpointSupport.OwnedGroup(db, groupId, user);
            if (group is null) return Results.Forbid();
            if (group.Version != request.ExpectedVersion) return Results.Conflict(new { group.Version });
            if (!group.Members.Any(x => x.UserId == request.UserId)) group.Members.Add(new GroupMember { GroupId = group.Id, UserId = request.UserId, Role = GroupRole.Member });
            group.Version++;
            await db.SaveChangesAsync();
            return Results.NoContent();
        });
        routes.MapDelete("/{groupId:guid}/members/{memberId:guid}", async (Guid groupId, Guid memberId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await EndpointSupport.OwnedGroup(db, groupId, user);
            if (group is null) return Results.Forbid();
            var member = group.Members.SingleOrDefault(x => x.Id == memberId && x.Role != GroupRole.Owner);
            if (member is null) return Results.NotFound();
            member.Status = MembershipStatus.Left;
            group.Version++;
            await db.SaveChangesAsync();
            return Results.NoContent();
        });
        return app;
    }
}
