using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Security.AccessControl;
using System.Text.RegularExpressions;
using System.Threading;
using Model;
using Model.Domain;
using Networking.DTO;
using Service.Service;

namespace Networking.protocol
{
    public class ServicesProxy :IService
    {
        private string host;
        private int port;

        private IRefereeObserver client;

        private NetworkStream stream;

        private IFormatter formatter;
        private TcpClient connection;

        private Queue<Response> responses;
        private volatile bool finished;
        private EventWaitHandle _waitHandle;
        public ServicesProxy(string host, int port)
        {
            this.host = host;
            this.port = port;
            responses = new Queue<Response>();
        }

        public Result AddResult(Participant participant, Trial trial,int points)
        {
            Result result = new Result(participant, trial,points);
            ResultDTO resultDto = DTOUtils.GetDTO(result);
            sendRequest(new AddResultRequest(resultDto));
            Response response = readResponse();
            if (response is ErrorResponse)
            {
                return null;
            }

            return result;
        }

        public int GetTotalPointsAtTrial(Participant participant, Trial trial)
        {
            ParticipantDTO participantDto = DTOUtils.GetDTO(participant);
            TrialDTO trialDto = DTOUtils.GetDTO(trial);
            ParticipantTrialData participantTrialData = new ParticipantTrialData(participantDto, trialDto);

            sendRequest(new PointsAtTrialRequest(participantTrialData));
            Response response = readResponse();
            if (response is PointsAtTrialResponse)
            {
                PointsAtTrialResponse pointsAtTrialResponse = (PointsAtTrialResponse)response;
                return pointsAtTrialResponse.points;
            }

            throw new Exception("There was an error");
        }


        public List<Participant> GetParticipants(Referee referee)
        {
            RefereeDTO refereeDto = DTOUtils.GetDTO(referee);
            Console.WriteLine(1);
            sendRequest(new GetParticipantsRequest(refereeDto));
            Console.WriteLine(2);
            Response response = readResponse();
            Console.WriteLine(response);
            if (response is ParticipantsResponse)
            {
                ParticipantsResponse participantsResponse = (ParticipantsResponse) response;
                return DTOUtils.GetFromDTO(participantsResponse.participants);
            }
            throw new Exception("There was an error");
        }
        public List<Participant> GetParticipantsAtTrial(Trial trial)
        {
            TrialDTO trialDto = DTOUtils.GetDTO(trial);
            sendRequest(new GetFilteredParticipantsRequest(trialDto));
            Response response = readResponse();
            if (response is FilteredParticipantsResponse)
            {
                FilteredParticipantsResponse participantsResponse = (FilteredParticipantsResponse) response;
                return DTOUtils.GetFromDTO(participantsResponse.participants);
            }
            throw new Exception("There was an error");
        }
        
        public void Logout(String username ,String password, IRefereeObserver client)
        {

            RefereeDTO refereeDto = new RefereeDTO(50, username, password, null);
            sendRequest(new LogoutRequest(refereeDto));
            Response response = readResponse();
            if (response is ErrorResponse)
            {
                ErrorResponse err = (ErrorResponse)response;
                throw new Exception(err.Message);
            }
            closeConnection();
        }
        
        
        public Referee Login (String username, String password, IRefereeObserver client)
        {
            initializeConnection();
            RefereeDTO refereeDto= new RefereeDTO(5,password, username,null);
            sendRequest(new LoginRequest(refereeDto));
            Response response = readResponse();
            if (response is RefereeResponse)
            {
                RefereeResponse refereeResponse = (RefereeResponse) response;
                Referee referee = DTOUtils.GetFromDTO(refereeResponse.refereeDto);
                this.client = client;
                return referee;
            }
            if (response is ErrorResponse)
            {
                ErrorResponse err = (ErrorResponse)response;
                closeConnection();
                throw new Exception(err.Message);
            }

            return null;
        }
        private void closeConnection()
        {
            finished = true;
            try
            {
                stream.Close();
                connection.Close();
                _waitHandle.Close();
                client = null;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }

        }
        private void sendRequest(Request request)
        {
            try
            {
                formatter.Serialize(stream, request);
                stream.Flush();
            }
            catch (Exception e)
            {
                throw new Exception("Error sending object " + e);
            }

        }

        private Response readResponse()
        {
            Response response = null;
            try
            {
                Console.WriteLine("aici a ajuns");
                _waitHandle.WaitOne();
                Console.WriteLine("aici nu");
                lock (responses)
                {
                    //Monitor.Wait(responses); 
                    response = responses.Dequeue();
                }


            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
            return response;
        }
        private void initializeConnection()
        {
            try
            {
                connection = new TcpClient(host, port);
                stream = connection.GetStream();
                formatter = new BinaryFormatter();
                finished = false;
                _waitHandle = new AutoResetEvent(false);
                startReader();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }
        private void startReader()
        {
            Thread tw = new Thread(run);
            tw.Start();
        }

        public virtual void run()
        {
            while (!finished)
            {
                try
                {
                    object response = formatter.Deserialize(stream);
                    Console.WriteLine("response received " + response);
                    if (response is ResultAddedResponse)
                    {
                        try
                        {
                            Task.Run(()=>client.Update());
                        }
                        catch (Exception e)
                        {
                            Console.WriteLine(e.StackTrace);
                        }
                    }
                    else
                    {

                        lock (responses)
                        {
                            responses.Enqueue((Response)response);
                            _waitHandle.Set();
                            

                        }
                        
                    }
                }
                catch (System.IO.IOException)
                {
                    finished = true;
                }
                catch (Exception e)
                {
                    Console.WriteLine("Reading error " + e);
                }

            }
        }
    }
    
}