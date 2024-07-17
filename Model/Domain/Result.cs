namespace Model.Domain;

public class Result : Entity<long>
{
    public Result(long id, Participant participant, Trial trial, int result) : base(id)
    {
        this.participant = participant;
        this.trial = trial;
        this.result = result;
    }

    public Participant participant{get;set;}
    public Trial trial{get; set; }
	public  int result{get; set; }

    public Result(Participant participant, Trial trial,int result) : base(0)
    {
        this.participant = participant;
        this.trial = trial;
		this.result = result;
    }

}