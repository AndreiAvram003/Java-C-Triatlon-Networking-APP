namespace Networking.DTO;

[Serializable]
public class ResultDTO
{
    public long Id { get; }
    public ParticipantDTO ParticipantDTO { get; }
    public TrialDTO TrialDTO { get; }
    public int Points { get; }

    public ResultDTO(long id, ParticipantDTO participantDTO, TrialDTO trialDTO, int points)
    {
        Id = id;
        ParticipantDTO = participantDTO;
        TrialDTO = trialDTO;
        Points = points;
    }
}
