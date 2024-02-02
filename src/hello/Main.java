package hello;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            TicketsWrapper ticketsWrapper = gson.fromJson(new FileReader("/Users/ivanryzov/Desktop/Control_work2/test/src/tickets.json"), TicketsWrapper.class);
            List<Ticket> tickets = ticketsWrapper.tickets;

            Map<String, List<Ticket>> ticketsByCarrier = new HashMap<>();
            List<Double> prices = new ArrayList<>();
            for (Ticket ticket : tickets) {
                if (ticket.origin.equals("VVO") && ticket.destination.equals("TLV")) {
                    ticketsByCarrier.computeIfAbsent(ticket.carrier, k -> new ArrayList<>()).add(ticket);
                    prices.add((double) ticket.price);
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            for (Map.Entry<String, List<Ticket>> entry : ticketsByCarrier.entrySet()) {
                long minDuration = Long.MAX_VALUE;
                for (Ticket ticket : entry.getValue()) {
                    long duration = format.parse(ticket.arrival_time).getTime() - format.parse(ticket.departure_time).getTime();
                    minDuration = Math.min(minDuration, duration);
                }
                System.out.println("Минимальное время полета для авиаперевозчика " + entry.getKey() + ": " + minDuration / (60 * 60 * 1000) + " часов");
            }

            prices.sort(Double::compareTo);
            double median = prices.size() % 2 == 0 ? (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1)) / 2 : prices.get(prices.size() / 2);
            double average = prices.stream().mapToDouble(a -> a).average().orElse(0.0);
            System.out.println("Разница между средней ценой и медианой: " + Math.abs(average - median));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}

class Ticket {
    String origin;
    String origin_name;
    String destination;
    String destination_name;
    String departure_date;
    String departure_time;
    String arrival_date;
    String arrival_time;
    String carrier;
    int stops;
    int price;
}

class TicketsWrapper {
    List<Ticket> tickets;
}
