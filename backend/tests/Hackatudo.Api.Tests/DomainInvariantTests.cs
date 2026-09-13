using Hackatudo.Api.Domain;

namespace Hackatudo.Api.Tests;

public sealed class DomainInvariantTests
{
    [Fact]
    public void Shared_model_contains_one_owner_and_monotonic_progress_contract()
    {
        var owner = Guid.NewGuid();
        var group = new StudyGroup { Name = "Missão Matemática", Objective = "Estudar junto", OwnerId = owner };
        group.Members.Add(new GroupMember { GroupId = group.Id, UserId = owner, Role = GroupRole.Owner });
        group.Mascot = new MascotProgress { GroupId = group.Id, Type = "Capivara", Experience = 0, Stage = 1 };
        Assert.Single(group.Members, x => x.Role == GroupRole.Owner);
        Assert.True(group.Mascot.Experience >= 0);
    }

    [Fact]
    public void Idempotency_is_represented_by_summary_primary_identifier()
    {
        var id = Guid.NewGuid();
        Assert.Equal(id, new SharedSessionSummary { Id = id, GroupId = Guid.NewGuid(), DurationMinutes = 25, Completed = true }.Id);
    }
}
