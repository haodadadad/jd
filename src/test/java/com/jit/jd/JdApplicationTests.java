package com.jit.jd;

import com.jit.jd.mapper.UserMapper;
import com.jit.jd.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JdApplicationTests {



    @Test
    void contextLoads()  {

    }

}
