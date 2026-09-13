using System.Security.Claims;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

public static class GroupSyncEndpoints
{
    public static IEndpointRouteBuilder MapGroupSyncEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapPut("/v1/groups/{groupId:guid}/ownership", async (Guid groupId, TransferOwnershipRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var group = await EndpointSupport.OwnedGroup(db, groupId, user);
            if (group is null) return Results.Forbid();
            if (group.Version != request.ExpectedVersion) return Results.Conflict(new { group.Version });
            var current = group.Members.Single(x => x.UserId == group.OwnerId && x.Role == GroupRole.Owner && x.Status == MembershipStatus.Active);
            var next = group.Members.SingleOrDefault(x => x.UserId == request.NewOwnerId && x.Status == MembershipStatus.Active);
            if (next is null) return Results.BadRequest();
            await using var transaction = await db.Database.BeginTransactionAsync();
            current.Role = GroupRole.Member;
            next.Role = GroupRole.Owner;
            group.OwnerId = next.UserId;
            group.Version++;
            await db.SaveChangesAsync();
            await transaction.CommitAsync();
            return Results.NoContent();
        }).RequireAuthorization();
        app.MapPost("/v1/session-summaries", async (SharedSessionSummaryRequest request, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            var allowed = await db.GroupMembers.AnyAsync(x => x.GroupId == request.GroupId && x.UserId == user.UserId() && x.Status == MembershipStatus.Active);
            if (!allowed || request.DurationMinutes < 0) return Results.UnprocessableEntity();
            if (await db.SessionSummaries.AnyAsync(x => x.Id == request.Id)) return Results.Accepted();
            db.SessionSummaries.Add(new SharedSessionSummary { Id = request.Id, GroupId = request.GroupId, DurationMinutes = request.DurationMinutes, Completed = request.Completed, OccurredAt = request.OccurredAt });
            var mascot = await db.Mascots.SingleAsync(x => x.GroupId == request.GroupId);
            if (request.Completed)
            {
                mascot.Experience += 25;
                mascot.Stage = mascot.Experience >= 250 ? 3 : mascot.Experience >= 100 ? 2 : 1;
                var goals = await db.GroupGoals.Where(x => x.GroupId == request.GroupId && x.Status == GoalStatus.Active).ToListAsync();
                foreach (var goal in goals)
                {
                    goal.CurrentValue++;
                    if (goal.CurrentValue >= goal.TargetValue) goal.Status = GoalStatus.Achieved;
                }
            }
            await db.SaveChangesAsync();
            return Results.Accepted();
        }).RequireAuthorization();
        return app;
    }
}
