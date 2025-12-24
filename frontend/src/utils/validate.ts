// 密码不少于6位，必须含有两个数字，不能都为大写或小写
export const validatePassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    return callback(new Error('请输入密码'))
  }
  if (value.length < 6) {
    return callback(new Error('密码长度不能少于6位'))
  }

  const digitCount = (value.match(/\d/g) || []).length
  if (digitCount < 2) {
    return callback(new Error('密码必须包含至少两个数字'))
  }

  // 假设包含字母才能区分大小写。
  // 逻辑：如果不等于全大写 且 不等于全小写，说明混合了大小写
  const isAllUpper = value === value.toUpperCase()
  const isAllLower = value === value.toLowerCase()
  
  // 增加一个正则判断是否包含字母，防止纯符号/数字绕过大小写判断
  const hasLetter = /[a-zA-Z]/.test(value)

  if (!hasLetter || isAllUpper || isAllLower) {
    return callback(new Error('密码不能全为大写或全为小写（需包含混合大小写字母）'))
  }

  callback()
}