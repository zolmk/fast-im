import com.feiyu.msgserver.communicate.proto.ClientProto;
import org.junit.Test;

/**
 * @author zhufeifei 2023/9/15
 **/

public class TestProtobuf {

    @Test
    public void testProto() {
        ClientProto.CInner inner = ClientProto.CInner.newBuilder().setMsg(ClientProto.Msg.newBuilder().setTo("zolmk").setFrom("zff")).build();
        System.out.println(inner);
        ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder(inner);
        builder.getMsgBuilder().setTo("fy");
        ClientProto.CInner other = builder.build();
        System.out.println(other);
    }
}
