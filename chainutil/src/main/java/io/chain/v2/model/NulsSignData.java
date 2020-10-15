/*
 * MIT License
 *
 * Copyright (c) 2017-2019 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.chain.v2.model;
import java.io.IOException;
import java.math.BigInteger;

import io.chain.v2.base.BaseNulsData;
import io.chain.v2.base.NulsByteBuffer;
import io.chain.v2.base.NulsHash;
import io.chain.v2.base.NulsOutputStreamBuffer;
import io.chain.v2.crypto.ECKey;
import io.chain.v2.crypto.HexUtil;
import io.chain.v2.exception.NulsException;
import io.chain.v2.parse.SerializeUtils;

/**
 * @author facjas
 */
public class NulsSignData extends BaseNulsData {

    /**
     * 签名字节组
     */
    protected byte[] signBytes;

    public NulsSignData() {
    }

    @Override
    public int size() {
        return SerializeUtils.sizeOfBytes(signBytes);
    }

    @Override
    public void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBytesWithLength(signBytes);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.signBytes = byteBuffer.readByLengthByte();
    }

    public byte[] getSignBytes() {
        return signBytes;
    }

    public void setSignBytes(byte[] signBytes) {
        this.signBytes = signBytes;
    }

    public NulsSignData sign(NulsHash nulsHash, BigInteger privkey) throws NulsException {
        ECKey ecKey = ECKey.fromPrivate(privkey);
        byte[] signBytes = ecKey.sign(nulsHash.getBytes());
        NulsSignData signData = new NulsSignData();
        signData.parse(signBytes, 0);
        return signData;
    }

    @Override
    public String toString() {
        try {
            return HexUtil.encode(serialize());
        } catch (IOException e) {
            return super.toString();
        }
    }
}
