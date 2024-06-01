package ua.kpi.resourcesflow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChannelWrapper {
    private List<Channel> channels = new ArrayList<>();
}
