// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: interfaces.proto

// Protobuf Java Version: 3.25.0
package com.feiyu.interfaces.idl;

/**
 * Protobuf type {@code com.feiyu.interfaces.idl.RouteReq}
 */
public final class RouteReq extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.feiyu.interfaces.idl.RouteReq)
    RouteReqOrBuilder {
private static final long serialVersionUID = 0L;
  // Use RouteReq.newBuilder() to construct.
  private RouteReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private RouteReq() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new RouteReq();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_RouteReq_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_RouteReq_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.feiyu.interfaces.idl.RouteReq.class, com.feiyu.interfaces.idl.RouteReq.Builder.class);
  }

  private int bitField0_;
  public static final int TO_FIELD_NUMBER = 1;
  private long to_ = 0L;
  /**
   * <code>int64 to = 1;</code>
   * @return The to.
   */
  @java.lang.Override
  public long getTo() {
    return to_;
  }

  public static final int MSG_FIELD_NUMBER = 2;
  private com.feiyu.base.proto.Messages.Msg msg_;
  /**
   * <code>.Msg msg = 2;</code>
   * @return Whether the msg field is set.
   */
  @java.lang.Override
  public boolean hasMsg() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>.Msg msg = 2;</code>
   * @return The msg.
   */
  @java.lang.Override
  public com.feiyu.base.proto.Messages.Msg getMsg() {
    return msg_ == null ? com.feiyu.base.proto.Messages.Msg.getDefaultInstance() : msg_;
  }
  /**
   * <code>.Msg msg = 2;</code>
   */
  @java.lang.Override
  public com.feiyu.base.proto.Messages.MsgOrBuilder getMsgOrBuilder() {
    return msg_ == null ? com.feiyu.base.proto.Messages.Msg.getDefaultInstance() : msg_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (to_ != 0L) {
      output.writeInt64(1, to_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeMessage(2, getMsg());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (to_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, to_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getMsg());
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.feiyu.interfaces.idl.RouteReq)) {
      return super.equals(obj);
    }
    com.feiyu.interfaces.idl.RouteReq other = (com.feiyu.interfaces.idl.RouteReq) obj;

    if (getTo()
        != other.getTo()) return false;
    if (hasMsg() != other.hasMsg()) return false;
    if (hasMsg()) {
      if (!getMsg()
          .equals(other.getMsg())) return false;
    }
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + TO_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTo());
    if (hasMsg()) {
      hash = (37 * hash) + MSG_FIELD_NUMBER;
      hash = (53 * hash) + getMsg().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.feiyu.interfaces.idl.RouteReq parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.feiyu.interfaces.idl.RouteReq parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.feiyu.interfaces.idl.RouteReq parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.feiyu.interfaces.idl.RouteReq prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.feiyu.interfaces.idl.RouteReq}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.feiyu.interfaces.idl.RouteReq)
      com.feiyu.interfaces.idl.RouteReqOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_RouteReq_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_RouteReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.feiyu.interfaces.idl.RouteReq.class, com.feiyu.interfaces.idl.RouteReq.Builder.class);
    }

    // Construct using com.feiyu.interfaces.idl.RouteReq.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getMsgFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      to_ = 0L;
      msg_ = null;
      if (msgBuilder_ != null) {
        msgBuilder_.dispose();
        msgBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.feiyu.interfaces.idl.Interfaces.internal_static_com_feiyu_interfaces_idl_RouteReq_descriptor;
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.RouteReq getDefaultInstanceForType() {
      return com.feiyu.interfaces.idl.RouteReq.getDefaultInstance();
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.RouteReq build() {
      com.feiyu.interfaces.idl.RouteReq result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.feiyu.interfaces.idl.RouteReq buildPartial() {
      com.feiyu.interfaces.idl.RouteReq result = new com.feiyu.interfaces.idl.RouteReq(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.feiyu.interfaces.idl.RouteReq result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.to_ = to_;
      }
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.msg_ = msgBuilder_ == null
            ? msg_
            : msgBuilder_.build();
        to_bitField0_ |= 0x00000001;
      }
      result.bitField0_ |= to_bitField0_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.feiyu.interfaces.idl.RouteReq) {
        return mergeFrom((com.feiyu.interfaces.idl.RouteReq)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.feiyu.interfaces.idl.RouteReq other) {
      if (other == com.feiyu.interfaces.idl.RouteReq.getDefaultInstance()) return this;
      if (other.getTo() != 0L) {
        setTo(other.getTo());
      }
      if (other.hasMsg()) {
        mergeMsg(other.getMsg());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              to_ = input.readInt64();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 18: {
              input.readMessage(
                  getMsgFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private long to_ ;
    /**
     * <code>int64 to = 1;</code>
     * @return The to.
     */
    @java.lang.Override
    public long getTo() {
      return to_;
    }
    /**
     * <code>int64 to = 1;</code>
     * @param value The to to set.
     * @return This builder for chaining.
     */
    public Builder setTo(long value) {

      to_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int64 to = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearTo() {
      bitField0_ = (bitField0_ & ~0x00000001);
      to_ = 0L;
      onChanged();
      return this;
    }

    private com.feiyu.base.proto.Messages.Msg msg_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.feiyu.base.proto.Messages.Msg, com.feiyu.base.proto.Messages.Msg.Builder, com.feiyu.base.proto.Messages.MsgOrBuilder> msgBuilder_;
    /**
     * <code>.Msg msg = 2;</code>
     * @return Whether the msg field is set.
     */
    public boolean hasMsg() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>.Msg msg = 2;</code>
     * @return The msg.
     */
    public com.feiyu.base.proto.Messages.Msg getMsg() {
      if (msgBuilder_ == null) {
        return msg_ == null ? com.feiyu.base.proto.Messages.Msg.getDefaultInstance() : msg_;
      } else {
        return msgBuilder_.getMessage();
      }
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public Builder setMsg(com.feiyu.base.proto.Messages.Msg value) {
      if (msgBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        msg_ = value;
      } else {
        msgBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public Builder setMsg(
        com.feiyu.base.proto.Messages.Msg.Builder builderForValue) {
      if (msgBuilder_ == null) {
        msg_ = builderForValue.build();
      } else {
        msgBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public Builder mergeMsg(com.feiyu.base.proto.Messages.Msg value) {
      if (msgBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          msg_ != null &&
          msg_ != com.feiyu.base.proto.Messages.Msg.getDefaultInstance()) {
          getMsgBuilder().mergeFrom(value);
        } else {
          msg_ = value;
        }
      } else {
        msgBuilder_.mergeFrom(value);
      }
      if (msg_ != null) {
        bitField0_ |= 0x00000002;
        onChanged();
      }
      return this;
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public Builder clearMsg() {
      bitField0_ = (bitField0_ & ~0x00000002);
      msg_ = null;
      if (msgBuilder_ != null) {
        msgBuilder_.dispose();
        msgBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public com.feiyu.base.proto.Messages.Msg.Builder getMsgBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getMsgFieldBuilder().getBuilder();
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    public com.feiyu.base.proto.Messages.MsgOrBuilder getMsgOrBuilder() {
      if (msgBuilder_ != null) {
        return msgBuilder_.getMessageOrBuilder();
      } else {
        return msg_ == null ?
            com.feiyu.base.proto.Messages.Msg.getDefaultInstance() : msg_;
      }
    }
    /**
     * <code>.Msg msg = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.feiyu.base.proto.Messages.Msg, com.feiyu.base.proto.Messages.Msg.Builder, com.feiyu.base.proto.Messages.MsgOrBuilder> 
        getMsgFieldBuilder() {
      if (msgBuilder_ == null) {
        msgBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.feiyu.base.proto.Messages.Msg, com.feiyu.base.proto.Messages.Msg.Builder, com.feiyu.base.proto.Messages.MsgOrBuilder>(
                getMsg(),
                getParentForChildren(),
                isClean());
        msg_ = null;
      }
      return msgBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.feiyu.interfaces.idl.RouteReq)
  }

  // @@protoc_insertion_point(class_scope:com.feiyu.interfaces.idl.RouteReq)
  private static final com.feiyu.interfaces.idl.RouteReq DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.feiyu.interfaces.idl.RouteReq();
  }

  public static com.feiyu.interfaces.idl.RouteReq getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<RouteReq>
      PARSER = new com.google.protobuf.AbstractParser<RouteReq>() {
    @java.lang.Override
    public RouteReq parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<RouteReq> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<RouteReq> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.feiyu.interfaces.idl.RouteReq getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
