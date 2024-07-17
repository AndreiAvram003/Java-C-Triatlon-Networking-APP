using Model.Domain;

namespace Networking.DTO;

public static class DTOUtils
    {
        public static Participant GetFromDTO(ParticipantDTO participantDTO)
        {
            long id = participantDTO.Id;
            string name = participantDTO.Name;
            int points = participantDTO.Points;
            return new Participant(id, name, points);
        }

        public static ParticipantDTO GetDTO(Participant participant)
        {
            long id = participant.id;
            string name = participant.name;
            int points = participant.points;
            return new ParticipantDTO(id, name, points);
        }

        public static Result GetFromDTO(ResultDTO resultDTO)
        {
            long id = resultDTO.Id;
            Participant participant = GetFromDTO(resultDTO.ParticipantDTO);
            TrialDTO trialDTO = resultDTO.TrialDTO;
            Trial trial = GetFromDTO(trialDTO);
            int points = resultDTO.Points;
            return new Result(id, participant, trial, points);
        }

        public static ResultDTO GetDTO(Result result)
        {
            long id = result.id;
            ParticipantDTO participantDTO = GetDTO(result.participant);
            TrialDTO trialDTO = GetDTO(result.trial);
            int points = result.result;
            return new ResultDTO(id, participantDTO, trialDTO, points);
        }

        public static Referee GetFromDTO(RefereeDTO refereeDTO)
        {
            long id = refereeDTO.Id;
            string password = refereeDTO.Password;
            string name = refereeDTO.Name;
            TrialDTO trialDTO = refereeDTO.TrialDTO;
            List<ParticipantDTO> participantDTOs = trialDTO.Participants;
            List<Participant> participants = participantDTOs.Select(GetFromDTO).ToList();
            Trial trial = new Trial(trialDTO.Id, null, participants, trialDTO.Name);
            Referee referee = new Referee(id, name, password, trial);
            trial.referee = referee;
            return referee;
        }

        public static RefereeDTO GetDTO(Referee referee)
        {
            long id = referee.id;
            string password = referee.password;
            string name = referee.name;
            RefereeDTO refereeDTO = new RefereeDTO(id, password, name, null);
            List<ParticipantDTO> participantDTOs = GetDTO(referee.trial.participants);
            TrialDTO trialDTO = new TrialDTO(referee.trial.id, referee.trial.nume, refereeDTO, participantDTOs);
            refereeDTO.TrialDTO = trialDTO;

            return refereeDTO;
        }

        public static TrialDTO GetDTO(Trial trial)
        {
            long id = trial.id;
            string name = trial.nume;
            List<Participant> participants = trial.participants;
            List<ParticipantDTO> participantDTOs = GetDTO(participants);
            TrialDTO trialDTO = new TrialDTO(id, name, null, participantDTOs);
            RefereeDTO refereeDTO = new RefereeDTO(trial.referee.id, trial.referee.password, trial.referee.name, trialDTO);
            trialDTO.RefereeDTO = refereeDTO;
            return trialDTO;
        }

        public static Trial GetFromDTO(TrialDTO trialDTO)
        {
            long id = trialDTO.Id;
            string name = trialDTO.Name;
            RefereeDTO refereeDTO = trialDTO.RefereeDTO;
            Referee referee = new Referee(refereeDTO.Id, refereeDTO.Name, refereeDTO.Password, null);
            List<ParticipantDTO> participantDTOs = trialDTO.Participants;
            List<Participant> participants = participantDTOs.Select(GetFromDTO).ToList();
            Trial trial = new Trial(id, referee, participants, name);
            referee.trial = trial;
            return trial;
        }

        public static List<ParticipantDTO> GetDTO(List<Participant> participants)
        {
            return participants.Select(GetDTO).ToList();
        }

        public static List<Participant> GetFromDTO(List<ParticipantDTO> participants)
        {
            return participants.Select(GetFromDTO).ToList();
        }
    }
