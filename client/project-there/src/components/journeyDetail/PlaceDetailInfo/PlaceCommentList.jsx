import React, { useState, useRef, useEffect } from 'react';
import styled from 'styled-components';
import { useInView } from 'react-intersection-observer';
import DotLoader from 'react-spinners/DotLoader';
import PlaceComment from './PlaceComment';
import { useAuthValue } from '../../../contexts/AuthContext';
import PlaceCommentInput from './PlaceCommentInput';
import PlaceCommentFix from './PlaceCommentFix';
import {
  checkCommentList,
  getCommentListFromServer,
  deleteCommentFromCommentList,
} from '../../../lib/hooks/useJourneyDetail';

const conditionalChain = (condition, then, otherwise) =>
  condition ? then : otherwise;

const StyledCommentHolder = styled.ul`
  margin-top: ${(props) => (props.isTextDisplayOverflowed ? '50px' : '15px')};
  height: ${(props) =>
    props.displayCommentOverflowed
      ? '60%'
      : conditionalChain(props.commentOverflowed, '33%', '37%')};
  border-bottom: ${(props) =>
    !props.commentOverflowed && '0.5px solid #bcc4c6'};
  position: ${(props) =>
    props.displayCommentOverflowed ? 'absolute' : 'relative'};
  background-color: var(--color-gray100);
  top: ${(props) => props.displayCommentOverflowed && '18%'};
  left: ${(props) => props.displayCommentOverflowed && '10px'};
  width: 100%;
  .totalCommentCount {
    z-index: 10;
    margin-top: ${(props) => (props.contentOverflowed ? '10px' : '2px')};
    margin-bottom: 12px;
    left: 10px;
    font-size: var(--font-micro);
  }
  span {
    color: var(--color-gray400);
    font-size: var(--font-micro);
  }
  overflow: ${(props) => (props.displayCommentOverflowed ? 'auto' : 'hidden')};
  display: flex;
  flex-direction: column;
`;
const StyledContentDetail = styled.div`
  color: #bcc4c6;
  font-size: 12px;
  font-weight: 500;
  width: 100%;
  position: ${(props) =>
    props.displayCommentOverflowed ? 'absolute' : 'relative'};
  top: ${(props) => !props.displayCommentOverflowed && '10px'};
  bottom: ${(props) => props.displayCommentOverflowed && '14%'};
  cursor: pointer;
  background-color: var(--color-gray100);
  height: 20px;
  border-bottom: 0.5px solid var(--color-gray400);
`;
const StyledDotLoaderHolder = styled.div`
  display: 'block';
  margin-top: 20px;
  margin-left: 50%;
`;

export default function PlaceCommentList({
  isTextOverflowed,
  placeId,
  isTextDisplayOverflowed,
}) {
  const [isCommentOverflowed, setIsCommentOverflowed] = useState(false);
  const [displayCommentOverflowed, setDisplayCommentOverflowed] =
    useState(false);
  const [comments, setComments] = useState([]);
  const [initialComments, setInitialComments] = useState([]);
  const commentRef = useRef();
  const userInfo = useAuthValue();
  const commentListRef = useRef(comments);
  const initialCommentListRef = useRef();
  const [deleteTarget, setdeleteTarget] = useState(0);
  const [fixTarget, setFixTarget] = useState(0);
  const [totalComments, setTotalComments] = useState(0);
  const [page, setPage] = useState(0);
  const [ref, inView] = useInView();
  const [isLastPage, setIsLastPage] = useState(false);
  const getCommentParams = {
    placeId,
    page,
    setIsLastPage,
    setComments,
    setTotalComments,
    setInitialComments,
  };
  const handleCommentClick = () => {
    setDisplayCommentOverflowed((pre) => !pre);
  };

  useEffect(() => {
    getCommentListFromServer(getCommentParams);
    return () => {
      checkCommentList(initialCommentListRef.current, commentListRef.current);
    };
  }, []);

  useEffect(() => {
    // dotloader??? ?????? ???, page ?????? ???????????????
    if (inView) {
      setPage((prev) => prev + 1);
    }
  }, [inView]);

  useEffect(() => {
    // ???????????? ?????? ???, ??????????????? ???????????? ??? ?????????
    if (page !== 0) {
      getCommentListFromServer(getCommentParams);
    }
  }, [page]);

  // ????????? ????????? ??? ????????? ???????????????
  useEffect(() => {
    if (commentRef && !displayCommentOverflowed) {
      const overFlowCheck =
        commentRef.current.scrollHeight > commentRef.current.clientHeight;
      setIsCommentOverflowed(overFlowCheck);
    }
  }, [commentRef, displayCommentOverflowed, comments]);

  // Comment??? ?????? ????????? ?????? commentListRef??? ????????????
  useEffect(() => {
    commentListRef.current = comments;
  }, [comments]);
  // initialComments??? ?????? ????????? ?????? initialCommentListRef??? ????????????
  useEffect(() => {
    initialCommentListRef.current = initialComments;
  }, [initialComments]);

  // deleteButton??? ????????? ???, ????????? ????????? ??????
  useEffect(() => {
    if (deleteTarget !== 0) {
      const newCommentList = deleteCommentFromCommentList(comments, deleteTarget);
      setComments(newCommentList);
      setTotalComments((prev) => prev - 1);
    }
  }, [deleteTarget]);
  // ????????? ???????????? 10??? ????????? ????????? ??? ????????????
  useEffect(() => {
    if (totalComments <= 10 && displayCommentOverflowed) {
      if (!isLastPage) {
        setIsLastPage(true);
      }
    }
  }, [totalComments, displayCommentOverflowed]);

  return (
    <>
      <StyledCommentHolder
        textOverflowed={isTextOverflowed}
        commentOverflowed={isCommentOverflowed}
        isTextDisplayOverflowed={isTextDisplayOverflowed}
        displayCommentOverflowed={displayCommentOverflowed}
        ref={commentRef}
      >
        <div className="totalCommentCount">?????? {totalComments}</div>
        {comments?.length === 0 && <span>????????? ????????????.</span>}
        {comments?.length !== 0 &&
          comments.map((comment) => (
            <PlaceComment
              key={`${comment?.commentId}-${comment?.text}`}
              comment={comment}
              controlDelete={setdeleteTarget}
              controlFix={setFixTarget}
              userNickName={userInfo?.nickName}
            />
          ))}
        {fixTarget !== 0 && (
          <PlaceCommentFix
            controlComments={[comments, setComments]}
            controlFixtarget={[fixTarget, setFixTarget]}
          />
        )}
        {displayCommentOverflowed && !isLastPage && (
          <StyledDotLoaderHolder ref={ref}>
            <DotLoader size="20px" color="#51A863" />
          </StyledDotLoaderHolder>
        )}
      </StyledCommentHolder>
      {!!isCommentOverflowed && (
        <StyledContentDetail
          onClick={handleCommentClick}
          displayCommentOverflowed={displayCommentOverflowed}
        >
          {!displayCommentOverflowed ? '?????????' : '??????'}
        </StyledContentDetail>
      )}
      <PlaceCommentInput
        placeId={placeId}
        userInfo={userInfo}
        setComments={setComments}
        setTotalComments={setTotalComments}
      />
    </>
  );
}
