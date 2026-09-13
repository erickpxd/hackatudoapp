using System.Net;
using System.Net.Http.Json;
using Hackatudo.Api.Application;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.Extensions.DependencyInjection;

namespace Hackatudo.Api.Tests;

public sealed class AuthTests(ApiFactory factory) : IClassFixture<ApiFactory>
{
    [Fact]
    public async Task Login_returns_token_for_valid_credentials()
    {
        var response = await factory.CreateClient().PostAsJsonAsync("/v1/auth/login", new LoginRequest("demo@hackatudo.local", "Demonstracao!2026"));
        var result = await response.Content.ReadFromJsonAsync<AuthResult>();
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.False(string.IsNullOrWhiteSpace(result?.AccessToken));
    }

    [Fact]
    public async Task Login_rejects_invalid_credentials_and_database_stores_only_hash()
    {
        var response = await factory.CreateClient().PostAsJsonAsync("/v1/auth/login", new LoginRequest("demo@hackatudo.local", "wrong-password"));
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
        using var scope = factory.Services.CreateScope();
        var user = scope.ServiceProvider.GetRequiredService<HackatudoDbContext>().Users.Single();
        Assert.NotEqual("Demonstracao!2026", user.PasswordHash);
        Assert.DoesNotContain("Demonstracao!2026", user.PasswordHash);
    }
}
