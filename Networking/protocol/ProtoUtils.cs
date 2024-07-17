using Model.Domain;
using Networking.DTO;

namespace Networking.protocol;

    public class ProtoUtils
    {
        public static TriatlonProto.Response createOkResponse(RefereeDTO refereeDto,TrialDTO trialDto)
        {
            TriatlonProto.Response response = new TriatlonProto.Response {Type = TriatlonProto.Response.Types.Type.Ok};

            List<TriatlonProto.ParticipantDTO> participantDtos = new List<TriatlonProto.ParticipantDTO>();

            List<ParticipantDTO> participants = trialDto.Participants;
            foreach (var participant in participants)
            {
                TriatlonProto.ParticipantDTO participantDto = new TriatlonProto.ParticipantDTO
                {
                    Id = participant.Id,
                    Name = participant.Name,
                    Points = participant.Points
                };
                participantDtos.Add(participantDto);
            }
            
            TriatlonProto.TrialDTO trial = new TriatlonProto.TrialDTO
            {
                Id = trialDto.Id,
                Name = trialDto.Name,
                ParticipantsDto = { participantDtos },
                
            };
            
            Console.WriteLine("Trial:"+ trial);
            TriatlonProto.RefereeDTO referee = new TriatlonProto.RefereeDTO
            {
                Id = refereeDto.Id,
                Name = refereeDto.Name,
                Password = refereeDto.Password,
                TrialDto = trial
            };
            
            Console.WriteLine("am ajuns pe aici");
            Console.WriteLine(referee);
            
            if (referee != null)
                response.RefereeDto = referee;
            return response;
        }
        
        public static TriatlonProto.Response createErrorResponse(string text)
        {
            TriatlonProto.Response response = new TriatlonProto.Response() {Type = TriatlonProto.Response.Types.Type.Error, Error = text};
            return response;
        }

        public static TriatlonProto.Response createOkResponse(ResultDTO resultDto)
        {
            TriatlonProto.Response response = new TriatlonProto.Response {Type = TriatlonProto.Response.Types.Type.Ok};
            List<TriatlonProto.ParticipantDTO> participantDtos = new List<TriatlonProto.ParticipantDTO>();

            List<ParticipantDTO> participants = resultDto.TrialDTO.Participants;
            foreach (var participant in participants)
            {
                TriatlonProto.ParticipantDTO participantDto = new TriatlonProto.ParticipantDTO
                {
                    Id = participant.Id,
                    Name = participant.Name,
                    Points = participant.Points
                };
                participantDtos.Add(participantDto);
            }
            
            TriatlonProto.TrialDTO trial = new TriatlonProto.TrialDTO
            {
                Id = resultDto.TrialDTO.Id,
                Name = resultDto.TrialDTO.Name,
                ParticipantsDto = { participantDtos },
                
            };
            
            TriatlonProto.ParticipantDTO participantDto1 = new TriatlonProto.ParticipantDTO
            {
                Id = resultDto.ParticipantDTO.Id,
                Name = resultDto.ParticipantDTO.Name,
                Points = resultDto.ParticipantDTO.Points
            };
            
            TriatlonProto.ResultDTO result = new TriatlonProto.ResultDTO
            {
                Id = resultDto.Id,
                ParticipantDto = participantDto1,
                TrialDto = trial,
                Points = resultDto.Points
            };
            response.ResultDto = result;
            return response;
        }

        public static RefereeDTO getRefereeDTO(TriatlonProto.Request request)
        {
            RefereeDTO refereeDTO;
            
            List<ParticipantDTO> participants = new List<ParticipantDTO>();
            if (request.RefereeDto.TrialDto != null)
            {



                List<TriatlonProto.ParticipantDTO> participantsDto =
                    new List<TriatlonProto.ParticipantDTO>(request.RefereeDto.TrialDto.ParticipantsDto);
                foreach (var participantDto in participantsDto)
                {
                    ParticipantDTO participant =
                        new ParticipantDTO(participantDto.Id, participantDto.Name, participantDto.Points);
                    participants.Add(participant);
                }




                TrialDTO trialDTO = new TrialDTO(request.RefereeDto.TrialDto.Id, request.RefereeDto.TrialDto.Name, null,
                    participants);
                refereeDTO = new RefereeDTO(request.RefereeDto.Id, request.RefereeDto.Name,
                    request.RefereeDto.Password, trialDTO);
            }
            else{
                refereeDTO = new RefereeDTO(request.RefereeDto.Id, request.RefereeDto.Name,
                    request.RefereeDto.Password, null);
            }
            
            return refereeDTO;
        }

        public static ResultDTO getResult(TriatlonProto.Request request)
        {
            TriatlonProto.ResultDTO resultDto = request.ResultDto;
            ParticipantDTO participantDTO = new ParticipantDTO(resultDto.ParticipantDto.Id, resultDto.ParticipantDto.Name, resultDto.ParticipantDto.Points);
            List<ParticipantDTO> participants = new List<ParticipantDTO>();
            List<TriatlonProto.ParticipantDTO> participantsDto = new List<TriatlonProto.ParticipantDTO>(resultDto.TrialDto.ParticipantsDto);
            foreach (var participantDto in participantsDto)
            {
                ParticipantDTO participant = new ParticipantDTO(participantDto.Id, participantDto.Name, participantDto.Points);
                participants.Add(participant);
            }
            RefereeDTO refereeDTO = new RefereeDTO(resultDto.TrialDto.RefereeDto.Id, resultDto.TrialDto.RefereeDto.Name, resultDto.TrialDto.RefereeDto.Password, null);
            TrialDTO trialDTO = new TrialDTO(resultDto.TrialDto.Id, resultDto.TrialDto.Name, refereeDTO, participants);
            refereeDTO.TrialDTO = trialDTO;
            ResultDTO result = new ResultDTO(resultDto.Id, participantDTO, trialDTO, resultDto.Points);
            return result;
            
        }

        public static TriatlonProto.Response getParticipants(List<Participant> participants)
        {
            TriatlonProto.Response response = new TriatlonProto.Response {Type = TriatlonProto.Response.Types.Type.Participants};
            List<TriatlonProto.ParticipantDTO> participantDtos = new List<TriatlonProto.ParticipantDTO>();
            foreach (var participant in participants)
            {
                TriatlonProto.ParticipantDTO participantDto = new TriatlonProto.ParticipantDTO
                {
                    Id = participant.id,
                    Name = participant.name,
                    Points = participant.points
                };
                participantDtos.Add(participantDto);
            }
            response.ParticipantsDto.AddRange(participantDtos);
            return response;

        }
        
        public static ParticipantTrialData getParticipantTrialData(TriatlonProto.Request request)
        {
            TriatlonProto.ParticipantTrialData participantTrialData = request.ParticipantTrialData;
            List<ParticipantDTO> participants = new List<ParticipantDTO>();
            List<TriatlonProto.ParticipantDTO> participantsDto = new List<TriatlonProto.ParticipantDTO>(participantTrialData.TrialDto.ParticipantsDto);
            foreach (var participantDto in participantsDto)
            {
                ParticipantDTO participant = new ParticipantDTO(participantDto.Id, participantDto.Name, participantDto.Points);
                participants.Add(participant);
            }
            ParticipantDTO participantDTO = new ParticipantDTO(participantTrialData.ParticipantDto.Id, participantTrialData.ParticipantDto.Name, participantTrialData.ParticipantDto.Points);
            RefereeDTO refereeDTO = new RefereeDTO(participantTrialData.TrialDto.RefereeDto.Id, participantTrialData.TrialDto.RefereeDto.Name, participantTrialData.TrialDto.RefereeDto.Password, null);
            TrialDTO trialDTO = new TrialDTO(participantTrialData.TrialDto.Id, participantTrialData.TrialDto.Name, refereeDTO, participants);
            refereeDTO.TrialDTO = trialDTO;
            ParticipantTrialData participantTrialData1 = new ParticipantTrialData(participantDTO, trialDTO);
            return participantTrialData1;
        }

        public static TriatlonProto.Response resultAdded(Result result)
        {
            TriatlonProto.Response response = new TriatlonProto.Response {Type = TriatlonProto.Response.Types.Type.ResultAdded};
            return response;

        }

        public static TriatlonProto.Response createPointsResponse(int pointsAtTrial)
        {
            TriatlonProto.Response response = new TriatlonProto.Response {Type = TriatlonProto.Response.Types.Type.PointsAtTrial, Points = pointsAtTrial};
            return response;
        }
    }
