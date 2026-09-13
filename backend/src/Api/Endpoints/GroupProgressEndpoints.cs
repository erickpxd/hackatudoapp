using System.Security.Claims;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

public static class GroupProgressEndpoints
{
    public static IEndpointRouteBuilder MapGroupProgressEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapPost("/v1/groups/{groupId:guid}/goals", async (Guid groupId, CreateGoalRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await EndpointSupport.OwnedGroup(db, groupId, user);
            if (group is null) return Results.Forbid();
            if (group.Version != request.ExpectedVersion) return Results.Conflict(new { group.Version });
            if (request.TargetType != "NUMBER_OF_SESSIONS" || request.TargetValue < 1 || request.EndsAt <= request.StartsAt) return Results.BadRequest();
            var goal = new GroupGoal { GroupId = groupId, Title = request.Title.Trim(), TargetValue = request.TargetValue, StartsAt = request.StartsAt, EndsAt = request.EndsAt };
            group.Goals.Add(goal);
            group.Version++;
            await db.SaveChangesAsync();
            return Results.Created($"/v1/groups/{groupId}/goals/{goal.Id}", goal);
        }).RequireAuthorization();
        app.MapGet("/v1/groups/{groupId:guid}/mascot", async (Guid groupId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await db.Groups.FindAsync(groupId);
            if (group is null) return Results.NotFound();
            var isMember = await db.GroupMembers.AnyAsync(x => x.GroupId == groupId && x.UserId == user.UserId() && x.Status == MembershipStatus.Active);
            if (!isMember) return Results.Forbid();
            var mascot = await db.Mascots.SingleAsync(x => x.GroupId == groupId);
            return Results.Ok(mascot);
        }).RequireAuthorization();
        return app;
    }
}
