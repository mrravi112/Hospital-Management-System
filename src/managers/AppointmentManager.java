package managers;

import models.Appointment;
import java.util.ArrayList;
import java.io.*;


public class AppointmentManager {

    private static final String FILE   = "data/appointments.txt";
    private ArrayList<Appointment> list = new ArrayList<>();
    private int idCounter               = 1;

    public AppointmentManager() {
        new File("data").mkdirs();
        loadFromFile();
        idCounter = list.size() + 1;
    }

    private String nextId() { return "A" + String.format("%03d", idCounter); }

    public Appointment createAppointment(String patientId, String patientName,
                                         String doctorId, String doctorName,
                                         String date, String timeSlot) {
        Appointment a = new Appointment(nextId(), patientId, patientName,
                                        doctorId, doctorName, date, timeSlot, "Scheduled");
        idCounter++;
        list.add(a);
        saveToFile();
        return a;
    }

    public ArrayList<Appointment> getAll()   { return list; }
    public int count()                       { return list.size(); }

    public Appointment findById(String id) {
        for (Appointment a : list)
            if (a.getAppointmentId().equalsIgnoreCase(id)) return a;
        return null;
    }

    public boolean cancelById(String id) {
        Appointment a = findById(id);
        if (a == null) return false;
        a.setStatus("Cancelled"); saveToFile(); return true;
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, false))) {
            for (Appointment a : list) { bw.write(a.toFileString()); bw.newLine(); }
        } catch (IOException e) { System.out.println("Save error: " + e.getMessage()); }
    }

    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 8)
                    list.add(new Appointment(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]));
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.out.println("Load error: " + e.getMessage()); }
    }
}
