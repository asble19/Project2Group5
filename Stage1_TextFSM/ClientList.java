import java.io.*;
import java.util.*;

public class ClientList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Client> clients = new ArrayList<Client>();

    public void addClient(Client client) {
        clients.add(client);
    }

    public Client findClient(String clientID) {
        for (Client client : clients) {
            if (client.getClientID().equals(clientID)) {
                return client;
            }
        }
        return null;
    }

    public Iterator<Client> getClients() {
        return clients.iterator();
    }
}