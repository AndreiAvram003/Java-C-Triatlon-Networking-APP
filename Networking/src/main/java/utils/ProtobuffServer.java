package utils;

import objectprotocol.ClientWorker;
import objectprotocol.ProtoWorker;
import org.example.Service;

import java.net.Socket;

public class ProtobuffServer extends AbstractConcurrentServer{
    private Service projectServices;

    public ProtobuffServer(int port, Service projectServices) {
        super(port);
        this.projectServices = projectServices;
        System.out.println("Created concurrent server");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtoWorker worker = new ProtoWorker(projectServices, client);
        Thread th = new Thread(worker);
        return th;
    }

    @Override
    public void stop() {
        System.out.println("Stopping concurrent server");
    }
}
