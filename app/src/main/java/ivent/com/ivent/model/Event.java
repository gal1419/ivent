package ivent.com.ivent.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by galmalachi on 10/02/2018.
 */

public class Event implements Parcelable {

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

    protected Event(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        title = in.readString();
        address = in.readString();
        owner = (User) in.readValue(User.class.getClassLoader());
        if (in.readByte() == 0x01) {
            participants = new ArrayList<User>();
            in.readList(participants, User.class.getClassLoader());
        } else {
            participants = null;
        }
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(address);
        dest.writeValue(owner);
        if (participants == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(participants);
        }
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
         if (obj == this) {
             return true;
         }

        if (!(obj instanceof Event)) {
            return false;
        }

        Event e = (Event) obj;
        return Objects.equals(this.getId(), e.getId());
    }
}