package ca.innov8solutions.parachute.config;

import ca.innov8solutions.parachute.framework.ServerType;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;

import java.util.List;

@Data
@Singleton
public class MainConfig {

    private List<ServerType> types;

    public @Inject MainConfig() {
        this.types = Lists.newArrayList();
    }
}
