package models;


public class Appointment {

    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String date;         
    private String timeSlot;     
    private String status;       

    public Appointment(String appointmentId, String patientId, String patientName,
                       String doctorId, String doctorName,
                       String date, String timeSlot, String status) {
        this.appointmentId = appointmentId;
        this.patientId     = patientId;
        this.patientName   = patientName;
        this.doctorId      = doctorId;
        this.doctorName    = doctorName;
        this.date          = date;
        this.timeSlot      = timeSlot;
        this.status        = status;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId()     { return patientId; }
    public String getPatientName()   { return patientName; }
    public String getDoctorId()      { return doctorId; }
    public String getDoctorName()    { return doctorName; }
    public String getDate()          { return date; }
    public String getTimeSlot()      { return timeSlot; }
    public String getStatus()        { return status; }

    
    public void setStatus(String status) { this.status = status; }
    public void setDate(String date)     { this.date = date; }
    public void setTimeSlot(String slot) { this.timeSlot = slot; }

    public String toFileString() {
        return appointmentId + "," + patientId + "," + patientName + "," +
               doctorId + "," + doctorName + "," + date + "," + timeSlot + "," + status;
    }
}
