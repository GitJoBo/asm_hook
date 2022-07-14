package com.zhangyue.ireader.plugin_privacy.visitor

import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem
import com.zhangyue.ireader.plugin_privacy.util.CommonUtil
import com.zhangyue.ireader.plugin_privacy.util.Logger
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class PrivacyMethodVisitor extends AdviceAdapter {

    private String className

    private PrivacyClassVisitor classVisitor

    protected PrivacyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, PrivacyClassVisitor classVisitor) {
        super(api, methodVisitor, access, name, descriptor)
        this.className = classVisitor.className
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }


    @Override
    void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        PrivacyGlobalConfig.methodReplaceItemList.each { item ->
            if (check(item, opcode, owner, name, descriptor)) {
                if (PrivacyGlobalConfig.shouldInject) {
                    opcode = item.replaceOpcode
                    owner = item.replaceClass
                    name = item.replaceMethod
                    descriptor = item.replaceDesc
                }

                logSuccess(item, opcode, owner, name, descriptor)
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    static boolean check(MethodReplaceItem item, int opcode, String owner, String name, String descriptor) {
        return (item.targetOpcode == opcode
                && item.targetOwner == owner
                && item.targetMethod == name
                && item.targetDesc == descriptor)
    }

    /**
     * 静态扫描。记录包含隐私合规方法的类、方法、字节码指令等相关信息
     */
    void logSuccess(MethodReplaceItem item, int opcode, String owner, String name, String descriptor) {
        Logger.info("targetClass:" + className)
        Logger.info("==========replace insn success " +
                "${opcode} ${owner} ${name} ${descriptor} =======" +
                "to " +
                " ${item.replaceOpcode} ${item.replaceClass} ${item.replaceMethod} ${item.replaceDesc} "
        )
        PrivacyGlobalConfig.stringBuilder.append("targetClass= ${CommonUtil.path2ClassName(className)}")
        PrivacyGlobalConfig.stringBuilder.append("-->")
        PrivacyGlobalConfig.stringBuilder.append("invokeMethod= ${getName()}")
        PrivacyGlobalConfig.stringBuilder.append("-->")
        PrivacyGlobalConfig.stringBuilder.append("invokeMethod= ${methodDesc}")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("opcode=${item.targetOpcode}, owner=${item.targetOwner}, name=${item.targetMethod}, descriptor=${item.targetDesc}")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append('------>')
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("opcode=${item.replaceOpcode}, owner=${item.replaceClass}, name=${item.replaceMethod}, descriptor=${item.replaceDesc}")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        Logger.info "PrivacyGlobalConfig.stringBuilder::${PrivacyGlobalConfig.stringBuilder.toString()}"
    }

}