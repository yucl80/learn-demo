package test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Test {
    public void Hi() {
        Arrays.asList(new String[]{"aa","bb"}).stream().map(s-> String.format("s:%s",s)).collect(Collectors.joining());
    }
}
