using System.ComponentModel.DataAnnotations.Schema;

namespace Model.Domain;

[Table("participants")]

public class Participant : Entity<long>
{
    public Participant(long id, string name, int points) : base(id)
    {
        this.name = name;
        this.points = points;
    }
    [Column("name")]
    public string name { get; set; }
    [Column("points")]
    public int points { get; set; }
    
    
    public override string ToString()
    {
        return $"Participant: {name}, Points: {points}";
    }
}



   