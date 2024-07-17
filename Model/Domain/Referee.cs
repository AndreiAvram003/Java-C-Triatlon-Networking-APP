namespace Model.Domain;

public class Referee : Entity<long>
{
    public Referee(long id, string name, string password, Trial trial) : base(id)
    {
        this.name = name;
        this.password = password;
        this.trial = trial;
    }

    public string name{get;set;}
    public string password{get; set; }
    public Trial trial{get; set; }
    

}