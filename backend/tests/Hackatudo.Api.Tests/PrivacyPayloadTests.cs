using Hackatudo.Api.Application;

namespace Hackatudo.Api.Tests;

public sealed class PrivacyPayloadTests
{
    [Fact]
    public void Shared_summary_is_an_explicit_allowlist()
    {
        var names = typeof(SharedSessionSummaryRequest).GetProperties().Select(x => x.Name).ToHashSet(StringComparer.OrdinalIgnoreCase);
        Assert.Equal(new[] { "Completed", "DurationMinutes", "GroupId", "Id", "OccurredAt" }, names.Order());
        Assert.DoesNotContain("packageName", names);
        Assert.DoesNotContain("intention", names);
        Assert.DoesNotContain("usageStats", names);
        Assert.DoesNotContain("reason", names);
    }
}
