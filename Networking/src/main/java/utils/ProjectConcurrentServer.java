package utils;

import objectprotocol.ClientWorker;
import org.example.Service;

import java.net.Socket;

public class ProjectConcurrentServer extends AbstractConcurrentServer{
    private Service projectServices;

    public ProjectConcurrentServer(int port, Service projectServices) {
        super(port);
        this.projectServices = projectServices;
        System.out.println("Created concurrent server");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientWorker worker = new ClientWorker(projectServices, client);
        Thread th = new Thread(worker);
        return th;
    }

    @Override
    public void stop() {
        System.out.println("Stopping concurrent server");
    }
}
