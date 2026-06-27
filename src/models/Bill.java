package models;


public class Bill {

    private String billId;
    private String patientId;
    private String patientName;
    private double doctorFee;
    private double medicineCost;
    private double roomCharges;
    private double totalAmount;
    private String date;
    private String status; 

    public Bill(String billId, String patientId, String patientName,
                double doctorFee, double medicineCost, double roomCharges, String date) {
        this.billId       = billId;
        this.patientId    = patientId;
        this.patientName  = patientName;
        this.doctorFee    = doctorFee;
        this.medicineCost = medicineCost;
        this.roomCharges  = roomCharges;
        this.totalAmount  = doctorFee + medicineCost + roomCharges;
        this.date         = date;
        this.status       = "Unpaid";
    }

    
    public String getBillId()       { return billId; }
    public String getPatientId()    { return patientId; }
    public String getPatientName()  { return patientName; }
    public double getDoctorFee()    { return doctorFee; }
    public double getMedicineCost() { return medicineCost; }
    public double getRoomCharges()  { return roomCharges; }
    public double getTotalAmount()  { return totalAmount; }
    public String getDate()         { return date; }
    public String getStatus()       { return status; }

    public void setStatus(String status) { this.status = status; }

    public String toFileString() {
        return billId + "," + patientId + "," + patientName + "," +
               doctorFee + "," + medicineCost + "," + roomCharges + "," +
               totalAmount + "," + date + "," + status;
    }
}
