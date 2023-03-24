package team.zavod.di;

import org.junit.jupiter.api.Test;
import team.zavod.di.service.Service;

public class TestService {
    @Test
    void testService() {
        Service service = new Service();
        service.run();
    }
}
