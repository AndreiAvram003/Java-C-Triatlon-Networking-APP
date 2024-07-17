namespace Model.Domain;

public class Trial : Entity<long>
{
    public Trial(long id, Referee referee, List<Participant> participants,string nume) : base(id)
    {
        this.referee = referee;
        this.participants = participants;
        this.nume = nume;
    }

    public Referee referee{get;set;}
    public List<Participant> participants{get;set;}
    
    public string nume{get;set;}
    

  }