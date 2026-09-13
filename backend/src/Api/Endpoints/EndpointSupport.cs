using System.Security.Claims;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Api.Endpoints;

internal static class EndpointSupport
{
    public static Guid UserId(this ClaimsPrincipal principal) =>
        Guid.TryParse(principal.FindFirstValue(ClaimTypes.NameIdentifier) ?? principal.FindFirstValue("sub"), out var id) ? id : Guid.Empty;

    public static async Task<StudyGroup?> OwnedGroup(HackatudoDbContext db, Guid groupId, ClaimsPrincipal principal) =>
        await db.Groups.Include(x => x.Members).Include(x => x.Mascot).Include(x => x.Goals)
            .SingleOrDefaultAsync(x => x.Id == groupId && x.OwnerId == principal.UserId());
}
