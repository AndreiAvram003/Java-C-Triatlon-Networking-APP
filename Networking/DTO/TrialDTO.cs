namespace Networking.DTO;

[Serializable]

public class TrialDTO
{
    public long Id { get; set; }
    public string Name { get; set; }
    public RefereeDTO RefereeDTO { get; set; }
    public List<ParticipantDTO> Participants { get; set; }

    public TrialDTO(long id, string name, RefereeDTO refereeDTO, List<ParticipantDTO> participants)
    {
        Id = id;
        Name = name;
        RefereeDTO = refereeDTO;
        Participants = participants;
    }
}