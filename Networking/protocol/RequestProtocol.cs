using System;
using Networking.DTO;


namespace Networking.protocol
{
    public interface Request
    {
    }

    [Serializable]
    public class LoginRequest : Request
    {
        public RefereeDTO refereeDto { get; set; }

        public LoginRequest(RefereeDTO refereeDTO)
        {
            this.refereeDto = refereeDTO;

        }
    }

    [Serializable]
    public class LogoutRequest : Request
    {
        public RefereeDTO refereeDto { get; set; }

        public LogoutRequest(RefereeDTO refereeDTO)
        {
            this.refereeDto = refereeDTO;
        }
    }

    [Serializable]
    public class GetParticipantsRequest : Request
    {
        public RefereeDTO refereeDto { get; set; }

        public GetParticipantsRequest(RefereeDTO refereeDTO)
        {
            this.refereeDto = refereeDTO;
        }
    }

    [Serializable]
    public class GetFilteredParticipantsRequest : Request
    {
        public TrialDTO trialDto { get; set; }
        
        public GetFilteredParticipantsRequest(TrialDTO trialDTO)
        {
            this.trialDto = trialDTO;
        }
    }

    [Serializable]
    public class AddResultRequest : Request
    {
        public ResultDTO resultDto { get; set; }

        public AddResultRequest(ResultDTO resultDTO)
        {
            this.resultDto = resultDTO;
        }
    }

    [Serializable]
    public class PointsAtTrialRequest : Request
    {
        public ParticipantTrialData participantTrialData { get; set; }

        public PointsAtTrialRequest(ParticipantTrialData participantTrialData)
        {
            this.participantTrialData = participantTrialData;
        }
    }
}