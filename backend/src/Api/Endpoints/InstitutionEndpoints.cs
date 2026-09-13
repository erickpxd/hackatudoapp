using System.Security.Claims;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

public static class InstitutionEndpoints
{
    public static IEndpointRouteBuilder MapInstitutionEndpoints(this IEndpointRouteBuilder app)
    {
        var routes = app.MapGroup("/v1").RequireAuthorization();
        routes.MapGet("/schools", async (HackatudoDbContext db) => Results.Ok(await db.Schools.AsNoTracking().ToListAsync()));
        routes.MapPost("/schools", async (CreateNamedResourceRequest request, HackatudoDbContext db) =>
        {
            if (string.IsNullOrWhiteSpace(request.Name)) return Results.BadRequest();
            var school = new School { Name = request.Name.Trim() };
            db.Schools.Add(school);
            await db.SaveChangesAsync();
            return Results.Created($"/v1/schools/{school.Id}", school);
        });
        routes.MapGet("/schools/{schoolId:guid}/classrooms", async (Guid schoolId, HackatudoDbContext db) => Results.Ok(await db.Classrooms.AsNoTracking().Where(x => x.SchoolId == schoolId).ToListAsync()));
        routes.MapPost("/schools/{schoolId:guid}/classrooms", async (Guid schoolId, CreateNamedResourceRequest request, HackatudoDbContext db) =>
        {
            if (!await db.Schools.AnyAsync(x => x.Id == schoolId) || string.IsNullOrWhiteSpace(request.Name)) return Results.BadRequest();
            var classroom = new Classroom { SchoolId = schoolId, Name = request.Name.Trim() };
            db.Classrooms.Add(classroom);
            await db.SaveChangesAsync();
            return Results.Created($"/v1/schools/{schoolId}/classrooms/{classroom.Id}", classroom);
        });
        routes.MapGet("/classrooms/{classroomId:guid}/members", async (Guid classroomId, HackatudoDbContext db) =>
            Results.Ok(await db.ClassroomMembers.AsNoTracking().Where(x => x.ClassroomId == classroomId).ToListAsync()));
        routes.MapPost("/classrooms/{classroomId:guid}/members", async (Guid classroomId, AddClassroomMemberRequest request, HackatudoDbContext db) =>
        {
            if (!await db.Classrooms.AnyAsync(x => x.Id == classroomId) || !await db.Users.AnyAsync(x => x.Id == request.UserId)) return Results.BadRequest();
            if (await db.ClassroomMembers.AnyAsync(x => x.ClassroomId == classroomId && x.UserId == request.UserId)) return Results.Conflict();
            var member = new ClassroomMember { ClassroomId = classroomId, UserId = request.UserId };
            db.ClassroomMembers.Add(member);
            await db.SaveChangesAsync();
            return Results.Created($"/v1/classrooms/{classroomId}/members/{member.Id}", member);
        });
        routes.MapDelete("/classrooms/{classroomId:guid}/members/{memberId:guid}", async (Guid classroomId, Guid memberId, HackatudoDbContext db) =>
        {
            var member = await db.ClassroomMembers.SingleOrDefaultAsync(x => x.ClassroomId == classroomId && x.Id == memberId);
            if (member is null) return Results.NotFound();
            db.ClassroomMembers.Remove(member);
            await db.SaveChangesAsync();
            return Results.NoContent();
        });
        routes.MapGet("/aggregates/classrooms/{classroomId:guid}", async (Guid classroomId, ClaimsPrincipal user, HackatudoDbContext db) =>
        {
            if (!await db.Classrooms.AnyAsync(x => x.Id == classroomId)) return Results.NotFound();
            var summaries = await db.SessionSummaries.AsNoTracking().Where(x => x.ClassroomId == classroomId).ToListAsync();
            var count = summaries.Count;
            return Results.Ok(new ClassroomAggregateResponse(classroomId, count, count == 0 ? 0 : summaries.Average(x => x.DurationMinutes), count == 0 ? 0 : summaries.Count(x => x.Completed) / (double)count, 0, count == 0 ? "stable" : "improving"));
        });
        return app;
    }
}
