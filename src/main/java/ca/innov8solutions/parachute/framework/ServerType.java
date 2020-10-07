package ca.innov8solutions.parachute.framework;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class ServerType {

    private String name;
    private int minPort;
    private int maxPort;

    private List<String> args;
}
