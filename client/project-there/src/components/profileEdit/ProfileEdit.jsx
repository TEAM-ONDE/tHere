import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { useAuthValue } from '../../contexts/AuthContext';
import { checkNickNameAPI, updateUserInfoAPI } from '../../lib/apis/auth';
import ImageUploader from './ImageUploader';

const Wrapper = styled.div`
  display: flex;
  gap: 96px;
  justify-content: center;
  padding-top: 160px;
  font-size: var(--font-small);

  position: relative;
`;

const Form = styled.form`
  width: 330px;
  overflow: hidden;
  padding-bottom: 42px;

  position: relative;
`;

const InputLabel = styled.div`
  margin: 0 auto;
  margin-bottom: 4px;
  font-size: var(--font-micro);
  color: var(--color-gray500);
`;

const H2 = styled.h2`
  text-align: center;
  margin-bottom: 72px;
  font-size: var(--font-medium);
  font-weight: var(--weight-light);
`;

const SubmitButton = styled.button`
  display: block;
  margin: 28px auto;
  padding: 8px 24px;
  font-size: var(--font-small);
  background-color: var(--color-green200);
  color: var(--color-gray100);
  border: none;

  position: absolute;
  right: 0;
  bottom: -28px;
`;

const CancelButton = styled.button`
  display: block;
  margin: 28px auto;
  padding: 8px 24px;
  font-size: var(--font-small);
  background-color: var(--color-gray400);
  color: var(--color-gray100);
  border: none;

  position: absolute;
  right: 84px;
  bottom: -28px;
`;

const Row = styled.div`
  position: relative;
  width: 100%;
  margin: 0 auto;
  margin-bottom: 48px;
`;

const TextInput = styled.input`
  width: 100%;
  padding: 8px 0;
  border: 0;
  border-bottom: 1px solid var(--color-green200);
  background: none;
  font-size: var(--font-small);

  &:-webkit-autofill {
    -webkit-box-shadow: 0 0 0 1000px var(--color-gray100) inset;
    box-shadow: 0 0 0 1000px var(--color-gray100) inset;
    -webkit-text-fill-color: var(--color-green200);
  }
`;

const CheckButton = styled.button`
  line-height: 1;
  padding: 8px 16px;
  position: absolute;
  right: 0;
  color: 0.5px solid var(--color-green200);
  border: 0.5px solid var(--color-green200);
`;

const ValidationErrMsg = styled.div`
  color: red;
  position: absolute;
  bottom: -21px;
  left: 0;
  font-size: var(--font-micro);
`;

const PasswordChange = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48px;

  & * {
    display: block;
    position: static;
    margin: 0;
    width: inherit;
  }
`;

const validationErrorMessage = {
  password:
    '??????/??????/????????????, 1??? ????????? ?????????, ?????????, ??????, ???????????? ?????? (10~20???)',
  passwordConfirm: '??????????????? ???????????? ????????????.',
  nickName: '??????/??????/?????? (2~10???)',
};

// ????????? ??? ?????? ?????????
const emptyValueErrorMessage = {
  nickName: '???????????? ???????????? ???????????????.',
  password: '??????????????? ???????????? ???????????????.',
  passwordConfirm: '???????????? ????????? ???????????? ???????????????.',
};

function ProfileEdit({ editMode }) {
  console.log(editMode);

  // ????????? ?????? ?????? ??? ?????? ???????????? ?????? ??????
  const navigate = useNavigate();

  // ?????? ?????? ??????
  const userInfo = useAuthValue();
  // console.log(userInfo);

  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [nickNameMessage, setNickNameMessage] = useState('');

  const [checkNickName, setCheckNickName] = useState(false);

  // ???????????? ?????? ?????? on/off
  const [passwordEditMode, setPasswordEditMode] = useState(false);

  // ??? ????????? ?????? ??????
  const [userForm, setUserForm] = useState({
    id: '',
    password: '',
    passwordConfirm: '',
    email: '',
    name: '',
    nickName: '',
    profileImageUrl: '',
    profileImageFile: null,
  });

  // ??? ????????? ?????? ???????????? userForm ??? ????????????
  const handleChangeForm = ({ target }) => {
    setUserForm((prev) => ({
      ...prev,
      [target.name]: target.value,
    }));
  };

  const checkValidation = (target, regex, callback) => {
    if (!regex.test(target.value)) {
      callback(validationErrorMessage[target.name]);
    } else {
      callback('');
    }
  };

  const handlePasswordEditMode = () => {
    setPasswordEditMode(!passwordEditMode);
  };

  // ???????????? ????????? ??????
  const handlePasswordValidation = ({ target }) => {
    const passwordRegex =
      /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{10,20}$/;
    checkValidation(target, passwordRegex, setPasswordMessage);
  };

  // ???????????? ????????? ?????? ?????? ??????
  const handlePasswordConfirmValidation = ({ target }) => {
    if (userForm.password !== target.value) {
      setPasswordConfirmMessage('??????????????? ???????????? ????????????.');
    } else {
      setPasswordConfirmMessage('');
    }
  };

  // ????????? ????????? ??????
  const handleNickNameValidation = ({ target }) => {
    const nickNameRegex = /^[0-9a-zA-Z???-???]{2,10}$/;
    checkValidation(target, nickNameRegex, setNickNameMessage);
  };

  // ????????? ?????? ??????
  const handleCheckNickName = async (e) => {
    e.preventDefault();

    if (nickNameMessage) {
      return alert('????????? ????????? ??????????????????!');
    }

    try {
      const res = await checkNickNameAPI(userForm.nickName);

      if (res) {
        setCheckNickName(true);
        alert('??????????????? ?????????????????????!');
      } else {
        setCheckNickName(false);
        alert('????????? ??????????????????!');
      }
    } catch (err) {
      const { errMessage } = err.response.data;
      setCheckNickName(false);
      alert(errMessage);
    }
  };

  const handleModifyProfile = async (e) => {
    e.preventDefault();

    console.log(userForm);

    // ???????????? ?????? ?????? ?????? ?????????
    if (
      !userForm.password &&
      !userForm.profileImageFile &&
      userInfo.nickName === userForm.nickName
    ) {
      return alert('??????????????? ????????????.');
    }

    // ???????????? ????????? ?????? ?????????
    if (passwordEditMode) {
      if (!userForm.password) {
        return alert(emptyValueErrorMessage.password);
      }

      if (!userForm.passwordConfirm) {
        return alert(emptyValueErrorMessage.passwordConfirm);
      }
    }

    // ????????? ????????? ?????? ?????????
    if (!userForm.nickName) {
      return alert(emptyValueErrorMessage.nickName);
    }

    // ????????? ????????? ?????? ?????? ?????????
    if (nickNameMessage) {
      return alert('????????? ????????? ???????????????.');
    }

    // ????????? ?????? ?????? ?????? ?????????
    if (userForm.nickName !== userInfo.nickName && !checkNickName) {
      return alert('????????? ?????? ????????? ??????????????????');
    }

    try {
      const res = await updateUserInfoAPI(userForm);
      console.log(res);

      alert('????????? ?????????????????????.');
      navigate('/');
    } catch (err) {
      const { errCode, errMessage } = err.response.data;
      alert(errMessage);
      setCheckNickName(false);
    }
  };

  useEffect(() => {
    // ?????? ?????? ?????????
    Object.entries(userInfo).forEach(([key, value]) => {
      if (value) {
        setUserForm((prev) => ({ ...prev, [key]: value }));
      }
    });
  }, []);

  const handleClickCancel = () => {
    navigate(-1);
  };

  return (
    <Wrapper>
      {/* <H2>????????? ??????</H2> */}
      <div>
        {/* <InputLabel>????????? ?????????</InputLabel> */}
        <ImageUploader
          onChangeForm={setUserForm}
          imgUrl={userForm.profileImageUrl || ''}
        />
      </div>

      <Form>
        <InputLabel>?????????</InputLabel>
        <Row>
          <TextInput readOnly name="id" value={userForm.id} />
        </Row>
        {passwordEditMode ? (
          <>
            <InputLabel>????????????</InputLabel>
            <Row>
              <TextInput
                type="password"
                placeholder="????????????"
                name="password"
                onChange={handleChangeForm}
                onBlur={handlePasswordValidation}
              />
              <CheckButton type="button" onClick={handlePasswordEditMode}>
                ??????
              </CheckButton>
              {passwordMessage && (
                <ValidationErrMsg>{passwordMessage}</ValidationErrMsg>
              )}
            </Row>
            <InputLabel>???????????? ??????</InputLabel>
            <Row>
              <TextInput
                type="password"
                placeholder="???????????? ??????"
                name="passwordConfirm"
                onChange={handleChangeForm}
                onBlur={handlePasswordConfirmValidation}
              />
              {passwordConfirmMessage && (
                <ValidationErrMsg>{passwordConfirmMessage}</ValidationErrMsg>
              )}
            </Row>
          </>
        ) : (
          <PasswordChange>
            <InputLabel>????????????</InputLabel>
            <Row>
              <CheckButton type="button" onClick={handlePasswordEditMode}>
                ??????
              </CheckButton>
            </Row>
          </PasswordChange>
        )}

        <InputLabel>?????????</InputLabel>
        <Row>
          <TextInput readOnly name="email" value={userForm.email} />
        </Row>
        <InputLabel>??????</InputLabel>
        <Row>
          <TextInput readOnly name="name" value={userForm.name} />
        </Row>
        <InputLabel>?????????</InputLabel>
        <Row>
          <TextInput
            name="nickName"
            value={userForm.nickName}
            onChange={handleChangeForm}
            onBlur={handleNickNameValidation}
          />
          <CheckButton onClick={handleCheckNickName}>????????????</CheckButton>
          {nickNameMessage && (
            <ValidationErrMsg>{nickNameMessage}</ValidationErrMsg>
          )}
        </Row>
        <SubmitButton onClick={handleModifyProfile}>??????</SubmitButton>
        <CancelButton type="button" onClick={handleClickCancel}>
          ??????
        </CancelButton>
      </Form>
    </Wrapper>
  );
}

export default ProfileEdit;
