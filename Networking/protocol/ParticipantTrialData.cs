using Networking.DTO;

namespace Networking.protocol;

[Serializable]
public class ParticipantTrialData
{
    public ParticipantDTO ParticipantDTO { get; }
    public TrialDTO TrialDTO { get; }

    public ParticipantTrialData(ParticipantDTO participantDTO, TrialDTO trialDTO)
    {
        ParticipantDTO = participantDTO;
        TrialDTO = trialDTO;
    }
}