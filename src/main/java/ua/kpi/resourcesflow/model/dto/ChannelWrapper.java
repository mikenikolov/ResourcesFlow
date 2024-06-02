package ua.kpi.resourcesflow.model.dto;

import lombok.Data;
import ua.kpi.resourcesflow.model.Channel;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChannelWrapper {
    private List<Channel> channels = new ArrayList<>();
}
