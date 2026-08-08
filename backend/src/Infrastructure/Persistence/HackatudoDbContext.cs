using Hackatudo.Api.Domain;
using Microsoft.EntityFrameworkCore;

namespace Hackatudo.Api.Infrastructure.Persistence;

public sealed class HackatudoDbContext(DbContextOptions<HackatudoDbContext> options) : DbContext(options)
{
    public DbSet<User> Users => Set<User>();
    public DbSet<School> Schools => Set<School>();
    public DbSet<Classroom> Classrooms => Set<Classroom>();
    public DbSet<ClassroomMember> ClassroomMembers => Set<ClassroomMember>();
    public DbSet<StudyGroup> Groups => Set<StudyGroup>();
    public DbSet<GroupMember> GroupMembers => Set<GroupMember>();
    public DbSet<GroupInvite> GroupInvites => Set<GroupInvite>();
    public DbSet<GroupGoal> GroupGoals => Set<GroupGoal>();
    public DbSet<MascotProgress> Mascots => Set<MascotProgress>();
    public DbSet<SharedSessionSummary> SessionSummaries => Set<SharedSessionSummary>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>().HasIndex(x => x.Email).IsUnique();
        modelBuilder.Entity<GroupMember>().HasIndex(x => new { x.GroupId, x.UserId }).IsUnique();
        modelBuilder.Entity<GroupInvite>().HasIndex(x => x.Code).IsUnique();
        modelBuilder.Entity<ClassroomMember>().HasIndex(x => new { x.ClassroomId, x.UserId }).IsUnique();
        modelBuilder.Entity<StudyGroup>().HasMany(x => x.Members).WithOne().HasForeignKey(x => x.GroupId).OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<StudyGroup>().HasOne(x => x.Mascot).WithOne().HasForeignKey<MascotProgress>(x => x.GroupId).OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<StudyGroup>().HasMany(x => x.Invites).WithOne().HasForeignKey(x => x.GroupId).OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<StudyGroup>().HasMany(x => x.Goals).WithOne().HasForeignKey(x => x.GroupId).OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<SharedSessionSummary>().HasKey(x => x.Id);
    }
}
