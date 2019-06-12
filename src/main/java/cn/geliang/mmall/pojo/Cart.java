package cn.geliang.mmall.pojo;

import lombok.*;

import java.util.Date;

//@Data // 包含@Getter, @Setter, @ToString, @HashCodeAndEquals
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = "updateTime")
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;
}

