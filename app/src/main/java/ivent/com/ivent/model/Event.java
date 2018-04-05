package ivent.com.ivent.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by galmalachi on 10/02/2018.
 */

public class Event {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("address")
    private String address;

    @SerializedName("owner")
    private User owner;

    @SerializedName("participants")
    private List<User> participants;

    @SerializedName("image")
    private String image;


    public Event() {

    }

    public Event(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

