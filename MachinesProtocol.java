import java.io.Serializable;

public class MachinesProtocol implements Serializable {
  private static final long serialVersionUID = 2496508428440758311L;
  private String message;
  private Integer destination;
  private Boolean isSendingToken;

  public MachinesProtocol(String message, Integer destination) {
    this.message = message;
    this.destination = destination;
    this.isSendingToken = false;
  }

  public MachinesProtocol(Integer destination) {
    this.message = null;
    this.destination = destination;
    this.isSendingToken = true;
  }

  public String getMessage() {
    return this.message;
  }

  public Integer getDestination() {
    return this.destination;
  }

  public Boolean getIsSendingToken() {
    return this.isSendingToken;
  }

  @Override
  public String toString() {
    return "message: " + this.message + "\ndestination: " + this.destination.toString() + "\nisSendingToken: "
        + this.isSendingToken.toString();
  }
}
