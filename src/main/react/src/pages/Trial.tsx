import React, {Component} from "react";
import Button from "../components/Button";
import './Trial.css';
import {Scale} from "./Scale";
import {RouteComponentProps} from "react-router";
import {server} from "../utils/server";

interface UrlParams {
    name: string;
}

interface Props extends RouteComponentProps<UrlParams> {
}

interface State {
    scale: Scale;
    trialName: string;
    selectedIndex?: number;
}

export class Trial extends Component<Props, State> {
    componentDidMount() {
        const {name} = this.props.match.params;
        this.setState({trialName: name});
        fetch(server.trial(name))
            .then(result => result.json())
            .then(json => this.fetchScale(json.scale));

        document.addEventListener("keypress", this.handleKeyPress, true);
    }

    componentWillUnmount() {
        document.removeEventListener("keypress", this.handleKeyPress);
    }

    private fetchScale(name: string) {
        fetch(server.scale(name))
            .then(result => result.json())
            .then(json => this.setState({scale: json}))
    }

    render() {
        return (this.state ? this.buttons(this.state.scale) : <span/>)
    }

    handleKeyPress: EventListener = (ev) => {
        const key = (ev as KeyboardEvent).key;
        const selectedIndex =
            key === "[" ? (this.state.selectedIndex !== undefined ? Math.max(0, this.state.selectedIndex - 1) : 0) :
                key === "]" ? (this.state.selectedIndex !== undefined ? Math.min(this.state.scale.intensities.length - 1, (this.state.selectedIndex + 1)) : 0) :
                    this.state.selectedIndex;
        this.setState({selectedIndex});

    }

    private buttons(scale: Scale) {
        return scale ? <div className="container">
            <div className="row col justify-content-center"><h1 className="trial">{scale.description}</h1></div>
            {scale.intensities.map((intensity, index) =>
                <div key={index} className="row align-items-center">
                    <div className="col"/>
                    <Button trial={this.state.trialName} intensity={intensity.number}
                            selected={this.state.selectedIndex !== undefined ? this.state.selectedIndex === index : false}/>
                    <div className="col">
                        <div className={"borg-button-label"}>{intensity.label}</div>
                    </div>
                    <div className="col"/>
                </div>)
            }
        </div> : <div/>
    }
}