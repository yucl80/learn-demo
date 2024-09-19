public class MethodEntity {
    private String name;
    private String signature;
    private String body;
    private int startByte;
    private int endByte;

    public MethodEntity(String name, String signature, String body, int startByte, int endByte) {
        this.name = name;
        this.signature = signature;
        this.body = body;
        this.startByte = startByte;
        this.endByte = endByte;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStartByte() {
        return startByte;
    }

    public void setStartByte(int startByte) {
        this.startByte = startByte;
    }

    public int getEndByte() {
        return endByte;
    }

    public void setEndByte(int endByte) {
        this.endByte = endByte;
    }

    @Override
    public String toString() {
        return "MethodEntity{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", startByte=" + startByte +
                ", endByte=" + endByte +
                '}';
    }
}

