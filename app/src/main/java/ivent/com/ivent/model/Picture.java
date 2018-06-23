package ivent.com.ivent.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by galmalachi on 06/04/2018.
 */

public class Picture {

    @SerializedName("id")
    private Long Id;

    @SerializedName("owner")
    private User owner;

    @SerializedName("description")
    private String description;

    @SerializedName("image")
    private byte[] image;

    public Picture() {

    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}