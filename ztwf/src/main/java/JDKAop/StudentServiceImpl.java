package JDKAop;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StudentServiceImpl implements StudentService {

    @Override
    public void insertStudent(){
        insert();
    }

    @Transactional(rollbackFor = Exception.class)
    public void insert() {
//        StudentDO studentDO = new StudentDO();
//        studentDO.setName("小民");
//        studentDO.setAge(22);
//        studentMapper.insert(studentDO);
//
//        if (studentDO.getAge() > 18) {
//            throw new RuntimeException("年龄不能大于18岁");
//        }
    }
}
