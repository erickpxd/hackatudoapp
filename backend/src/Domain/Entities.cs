using System.ComponentModel.DataAnnotations;

namespace Hackatudo.Api.Domain;

public enum GroupRole { Owner, Member }
public enum MembershipStatus { Active, Left }
public enum GoalStatus { Active, Achieved }

public sealed class User
{
    public Guid Id { get; set; } = Guid.NewGuid();
    [MaxLength(320)] public required string Email { get; set; }
    public required string PasswordHash { get; set; }
}

public sealed class School
{
    public Guid Id { get; set; } = Guid.NewGuid();
    [MaxLength(160)] public required string Name { get; set; }
    public List<Classroom> Classrooms { get; set; } = [];
}

public sealed class Classroom
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid SchoolId { get; set; }
    [MaxLength(160)] public required string Name { get; set; }
    public List<ClassroomMember> Members { get; set; } = [];
}

public sealed class ClassroomMember
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid ClassroomId { get; set; }
    public Guid UserId { get; set; }
}

public sealed class StudyGroup
{
    public Guid Id { get; set; } = Guid.NewGuid();
    [MaxLength(160)] public required string Name { get; set; }
    [MaxLength(500)] public required string Objective { get; set; }
    public Guid OwnerId { get; set; }
    public long Version { get; set; }
    public List<GroupMember> Members { get; set; } = [];
    public MascotProgress? Mascot { get; set; }
    public List<GroupInvite> Invites { get; set; } = [];
    public List<GroupGoal> Goals { get; set; } = [];
}

public sealed class GroupMember
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid GroupId { get; set; }
    public Guid UserId { get; set; }
    public GroupRole Role { get; set; }
    public MembershipStatus Status { get; set; } = MembershipStatus.Active;
}

public sealed class GroupInvite
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid GroupId { get; set; }
    [MaxLength(64)] public required string Code { get; set; }
    public DateTimeOffset ExpiresAt { get; set; }
    public bool Revoked { get; set; }
    public DateTimeOffset? ConsumedAt { get; set; }
}

public sealed class GroupGoal
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid GroupId { get; set; }
    [MaxLength(160)] public required string Title { get; set; }
    public int TargetValue { get; set; }
    public int CurrentValue { get; set; }
    public DateTimeOffset StartsAt { get; set; }
    public DateTimeOffset EndsAt { get; set; }
    public GoalStatus Status { get; set; }
}

public sealed class MascotProgress
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid GroupId { get; set; }
    [MaxLength(80)] public required string Type { get; set; }
    public long Experience { get; set; }
    public int Stage { get; set; } = 1;
}

public sealed class SharedSessionSummary
{
    public Guid Id { get; set; }
    public Guid GroupId { get; set; }
    public int DurationMinutes { get; set; }
    public bool Completed { get; set; }
    public DateTimeOffset OccurredAt { get; set; }
}
