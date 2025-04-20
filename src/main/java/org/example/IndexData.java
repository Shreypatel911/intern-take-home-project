package org.example;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class IndexData {
    String index;
    // using BitInteger here because I was not sure about the maximum size in bytes I will get in an input
    // and I thought that the size in bytes can be large enough that long can't handle
    BigInteger sizeInBytes;
    int numberOfShards;

    public IndexData(String index, BigInteger sizeInBytes, int numberOfShards){
        this.index = index;
        this.sizeInBytes = sizeInBytes;
        this.numberOfShards = numberOfShards;
    }

    public double getSizeInGB(){
        // bigInteger to bigDecimal
        BigDecimal bytes = new BigDecimal(sizeInBytes);
        BigDecimal gb = new BigDecimal("1000000000");
        // using two decimal points precision as in the example output also it is two
        // using half up rounding strategy means after decimal
        // .567 -> .057, .544 -> .54
        return bytes.divide(gb, 2, RoundingMode.HALF_UP).doubleValue();
    }

    public double getBalanceRatio(){
        return getSizeInGB() / (numberOfShards * 1.0 );
    }

    public int getRecommendedShardCount(){
        // taking ceil value
        // recommended shards for 2000 GB would be 67 not 66
        // 2000/30 = 66.67, but I will need one more extra shard to allocate those rest of the GBs
        // this example is given in the problem statement only
        return (int)Math.ceil(getSizeInGB() / 30.0);
    }
}
