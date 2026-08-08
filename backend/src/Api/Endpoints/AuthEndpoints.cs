using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Hackatudo.Api.Application;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

namespace Hackatudo.Api.Api.Endpoints;

public static class AuthEndpoints
{
    public static IEndpointRouteBuilder MapAuthEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapPost("/v1/auth/login", async (LoginRequest request, HackatudoDbContext db, IPasswordHasher<User> hasher, IConfiguration config) =>
        {
            var user = await db.Users.SingleOrDefaultAsync(x => x.Email == request.Email.ToLowerInvariant());
            if (user is null || hasher.VerifyHashedPassword(user, user.PasswordHash, request.Password) == PasswordVerificationResult.Failed)
                return Results.Unauthorized();

            var expiresAt = DateTimeOffset.UtcNow.AddHours(2);
            return Results.Ok(new AuthResult(CreateToken(user, expiresAt, config), expiresAt));
        }).AllowAnonymous();
        return app;
    }

    private static string CreateToken(User user, DateTimeOffset expiresAt, IConfiguration config)
    {
        var credentials = new SigningCredentials(
            new SymmetricSecurityKey(Encoding.UTF8.GetBytes(config["Jwt:SigningKey"]!)),
            SecurityAlgorithms.HmacSha256);
        return new JwtSecurityTokenHandler().WriteToken(new JwtSecurityToken(
            issuer: config["Jwt:Issuer"],
            audience: config["Jwt:Audience"],
            claims: [new Claim(JwtRegisteredClaimNames.Sub, user.Id.ToString()), new Claim(JwtRegisteredClaimNames.Email, user.Email)],
            expires: expiresAt.UtcDateTime,
            signingCredentials: credentials));
    }
}
