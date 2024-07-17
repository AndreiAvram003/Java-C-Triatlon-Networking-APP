using System;
using System.IO;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text.RegularExpressions;
using System.Threading;
using Model;
using Model.Domain;
using Networking.DTO;
using Service.Service;

namespace Networking.protocol
{
    public class ClientWorker : IRefereeObserver //, Runnable
    {
        private IService server;
        private TcpClient connection;

        private NetworkStream stream;
        private IFormatter formatter;
        private volatile bool connected;

        public ClientWorker(IService server, TcpClient connection)
        {
            this.server = server;
            this.connection = connection;
            try
            {
                stream = connection.GetStream();
                formatter = new BinaryFormatter();
                connected = true;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }



        private void sendResponse(Response response)
        {
            Console.WriteLine("sending response " + response);
            lock (stream)
            {
                formatter.Serialize(stream, response);
                stream.Flush();
            }

        }

        public virtual void run()
        {
            while (connected)
            {
                try
                {
                    object request = formatter.Deserialize(stream);
                    object response = handleRequest((Request)request);
                    if (response != null)
                    {
                        sendResponse((Response)response);
                    }
                }
                catch (SerializationException)
                {
                    connected = false;
                }
                catch (Exception e)
                {
                    Console.WriteLine(e);
                }

                try
                {
                    Thread.Sleep(1000);
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.StackTrace);
                }
            }

            try
            {
                stream.Close();
                connection.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine("Error " + e);
            }
        }
        private Response handleRequest(Request request)
        {
            Response response = null;

            if (request is LoginRequest)
            {
                Console.WriteLine("Login request...");
                LoginRequest loginRequest = (LoginRequest)request;
                RefereeDTO refereeDto = loginRequest.refereeDto;
                try
                {
                    lock (server)
                    {
                        Referee referee2 =server.Login(refereeDto.Name,refereeDto.Password, this);
                        return new RefereeResponse(DTOUtils.GetDTO(referee2));
                    }
                }
                catch (Exception e)
                {
                    connected = false;
                    return new ErrorResponse(e.Message);
                }
            }

            if (request is LogoutRequest)
            {
                Console.WriteLine("Logout request...");
                LogoutRequest logoutRequest = (LogoutRequest)request;
                RefereeDTO refereeDto = logoutRequest.refereeDto;
                Referee referee = DTOUtils.GetFromDTO(refereeDto);
                try
                {
                    lock (server)
                    {
                        server.Logout(referee.name,referee.password, this);
                    }
                    connected = false;
                }
                catch (Exception e)
                {
                    return new ErrorResponse(e.Message);
                }
            }

            if (request is GetParticipantsRequest)
            {
                Console.WriteLine("All participants request");
                GetParticipantsRequest all = (GetParticipantsRequest)request;
                RefereeDTO refereeDto = all.refereeDto;
                Referee referee = DTOUtils.GetFromDTO(refereeDto);
                try
                {
                    List<Participant> participants;
                    lock (server)
                    {
                        participants = server.GetParticipants(referee);
                    }
                    List<ParticipantDTO> participantsDto = DTOUtils.GetDTO(participants);

                    return new ParticipantsResponse(participantsDto);
                } catch (Exception e)
                {
                    return new ErrorResponse(e.Message);
                }
            }

            if (request is GetFilteredParticipantsRequest)
            {
                Console.WriteLine("Filtered participants request");
                GetFilteredParticipantsRequest all = (GetFilteredParticipantsRequest)request;
                TrialDTO trialDto = all.trialDto;
                Trial trial = DTOUtils.GetFromDTO(trialDto);
                try
                {
                    List<Participant> participants;
                    lock (server)
                    {
                        participants = server.GetParticipantsAtTrial(trial);
                    }
                    List<ParticipantDTO> participantsDto = DTOUtils.GetDTO(participants);

                    return new FilteredParticipantsResponse(participantsDto);
                } catch (Exception e)
                {
                    return new ErrorResponse(e.Message);
                }
            }

            if (request is AddResultRequest)
            {
                Console.WriteLine("Add result request...");
                AddResultRequest addResultRequest = (AddResultRequest)request;
                ResultDTO resultDto = addResultRequest.resultDto;
                Result result = DTOUtils.GetFromDTO(resultDto);
                try
                {
                    lock (server)
                    {
                        server.AddResult(result.participant,result.trial,result.result);
                    }
                    return new OkResponse(resultDto);
                }
                catch (Exception e)
                {
                    return new ErrorResponse(e.Message);
                }
            }


            if (request is PointsAtTrialRequest)
            {
                Console.WriteLine("Points at trial request...");
                PointsAtTrialRequest pointsAtTrialRequest = (PointsAtTrialRequest)request;
                ParticipantTrialData participantTrialData = pointsAtTrialRequest.participantTrialData;
                Participant participant = DTOUtils.GetFromDTO(participantTrialData.ParticipantDTO);
                Trial trial = DTOUtils.GetFromDTO(participantTrialData.TrialDTO);
                try
                {
                    int points;
                    lock (server)
                    {
                        points = server.GetTotalPointsAtTrial(participant,trial);
                    }
                    return new PointsAtTrialResponse(points);
                }
                catch (Exception e)
                {
                    return new ErrorResponse(e.Message);
                }
            }
           
            return response;
        }
        public void Update()
        {
            sendResponse(new ResultAddedResponse(null));
        }

    }
}