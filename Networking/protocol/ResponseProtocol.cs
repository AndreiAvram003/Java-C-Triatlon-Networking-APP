using System;
using Model;
using Networking.DTO;

namespace Networking.protocol
{
    public interface Response
    {
            
    }
    [Serializable]
    public class OkResponse : Response
    {
            private ResultDTO resultDto;
            public OkResponse(ResultDTO resultDto)
            {
                this.resultDto = resultDto;
            }
    }
    
        
    [Serializable]
    public class ErrorResponse : Response
    {
        private string message;

        public ErrorResponse(string message)
        {
            this.message = message;
        }

        public virtual string Message
        {
            get
            {
                return message;
            }
        }
    }
    [Serializable]
    public class RefereeResponse : Response
    {
        public RefereeDTO refereeDto { get; }

        public RefereeResponse(RefereeDTO refereeDto)
        {
            this.refereeDto = refereeDto;
        }
    }
    
    [Serializable]
    public class ParticipantsResponse : Response
    {
        public List<ParticipantDTO> participants { get; }

        public ParticipantsResponse(List<ParticipantDTO> participants)
        {
            this.participants = participants;
        }
    }
        
    [Serializable]
    public class FilteredParticipantsResponse : Response
    {
        public List<ParticipantDTO> participants { get; }

        public FilteredParticipantsResponse(List<ParticipantDTO> participants)
        {
            this.participants = participants;
        }
    }
        
    [Serializable]
    public class ResultAddedResponse : Response
    {
        public ResultDTO resultDto { get; }

        public ResultAddedResponse(ResultDTO resultDto)
        {
            this.resultDto = resultDto;
        }
    }
    
    [Serializable]
    
    public class PointsAtTrialResponse : Response
    {
        public int points { get; }

        public PointsAtTrialResponse(int points)
        {
            this.points = points;
        }
    }
    
}