namespace Hackatudo.Api.Application;

public sealed record LoginRequest(string Email, string Password);
public sealed record AuthResult(string AccessToken, DateTimeOffset ExpiresAt);
public sealed record CreateGroupRequest(string Name, string Objective, string MascotType);
public sealed record AddMemberRequest(Guid UserId, long ExpectedVersion);
public sealed record JoinInviteRequest(string InviteCode);
public sealed record CreateGoalRequest(string Title, string TargetType, int TargetValue, DateTimeOffset StartsAt, DateTimeOffset EndsAt, long ExpectedVersion);
public sealed record TransferOwnershipRequest(Guid NewOwnerId, long ExpectedVersion);
public sealed record CreateNamedResourceRequest(string Name);
public sealed record AddClassroomMemberRequest(Guid UserId);
public sealed record SharedSessionSummaryRequest(Guid Id, Guid GroupId, int DurationMinutes, bool Completed, DateTimeOffset OccurredAt);
public sealed record ClassroomAggregateResponse(Guid ClassroomId, int SessionCount, double AverageDurationMinutes, double CompletionRate, int InterventionCount, string Trend);
