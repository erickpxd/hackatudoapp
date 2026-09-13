using System.Security.Claims;
using System.Text;
using Hackatudo.Api.Api.Endpoints;
using Hackatudo.Api.Domain;
using Hackatudo.Api.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);
builder.Configuration.AddEnvironmentVariables("HACKATUDO_");
var signingKey = builder.Configuration["Jwt:SigningKey"] ?? throw new InvalidOperationException("Jwt signing key is required.");
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options =>
{
    options.MapInboundClaims = false;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidIssuer = builder.Configuration["Jwt:Issuer"],
        ValidateAudience = true,
        ValidAudience = builder.Configuration["Jwt:Audience"],
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(signingKey)),
        NameClaimType = "sub",
    };
});
builder.Services.AddAuthorization();
builder.Services.AddScoped<IPasswordHasher<User>, PasswordHasher<User>>();
if (builder.Environment.IsEnvironment("Testing"))
    builder.Services.AddDbContext<HackatudoDbContext>(options => options.UseInMemoryDatabase("hackatudo-tests"));
else
    builder.Services.AddDbContext<HackatudoDbContext>(options => options.UseNpgsql(builder.Configuration.GetConnectionString("Hackatudo")));

var app = builder.Build();
app.UseHttpsRedirection();
app.UseAuthentication();
app.UseAuthorization();
app.MapGet("/health", () => Results.Ok(new { status = "healthy" })).AllowAnonymous();
app.MapAuthEndpoints().MapGroupEndpoints().MapGroupInviteEndpoints().MapGroupProgressEndpoints().MapGroupSyncEndpoints().MapInstitutionEndpoints();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<HackatudoDbContext>();
    await db.Database.EnsureCreatedAsync();
    if (!await db.Users.AnyAsync())
    {
        var demo = new User { Email = "demo@hackatudo.local", PasswordHash = "pending" };
        demo.PasswordHash = scope.ServiceProvider.GetRequiredService<IPasswordHasher<User>>().HashPassword(demo, "Demonstracao!2026");
        db.Users.Add(demo);
        await db.SaveChangesAsync();
    }
}

app.Run();

public partial class Program;
