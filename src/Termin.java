public class Termin {
    
    String date, time, place, contact, category, topic;

    public Termin(String date, String time, String place, String contact, String category, String topic) {
        this.date = date;
        this.time = time;
        this.place = place;
        this.contact = contact;
        this.category = category;
        this.topic = topic;    
    }

    @Override
    public String toString() {
        return String.format("%s  |  %s  |  %s  |  %s  |  %s  |  %s", date, time, category, topic, place, contact);
    }
}
