namespace Networking.DTO;
[Serializable]

public class ParticipantDTO
{
    public long Id { get; set; }
    public string Name { get; set; }
    public int Points { get; set; }

    public ParticipantDTO(long id, string name, int points)
    {
        Id = id;
        Name = name;
        Points = points;
    }
}
